package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.api.bean.session.BusUser;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.main.constant.MainConstant;
import com.gt.union.main.entity.*;
import com.gt.union.main.mapper.UnionMainCreateMapper;
import com.gt.union.main.service.*;
import com.gt.union.main.vo.UnionMainChargeVO;
import com.gt.union.main.vo.UnionMainCreateVO;
import com.gt.union.main.vo.UnionMainVO;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.member.vo.UnionMemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 创建联盟 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMainCreateServiceImpl extends ServiceImpl<UnionMainCreateMapper, UnionMainCreate> implements IUnionMainCreateService {

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private IDictService dictService;

    @Autowired
    private IUnionMainPermitService unionMainPermitService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMainChargeService unionMainChargeService;

    @Autowired
    private IUnionMainDictService unionMainDictService;

    @Override
    public boolean hasCreateUnionMain(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        UnionMainCreate unionMainCreate = this.selectOne(entityWrapper);
        return unionMainCreate != null ? true : false;
    }

    /**
     * 根据商家id创建联盟
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> instanceByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        return checkInstancePermit(busId);
    }

    //检查盟主服务许可
    private Map<String, Object> checkInstancePermit(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        // 1、判断商家是否已是盟主
        if (this.unionMemberService.isUnionOwner(busId)) {
            throw new BusinessException("您已是联盟盟主，不可创建");
        }
        //2、判断是否有创建联盟的权限
        BusUser busUser = this.busUserService.getBusUserById(busId);
        if (busUser == null) {
            throw new BusinessException("账号不存在");
        }
        if (this.busUserService.isBusUserValid(busUser)) {
            throw new BusinessException(CommonConstant.UNION_BUS_OVERDUE_MSG);
        }
        List<Map> createDict = dictService.getCreateUnionDict();//创建联盟的权限
        boolean flag = false;
        Map info = null;
        for (Map map : createDict) {
            if (CommonUtil.toInteger(map.get("item_key")).equals(busUser.getLevel())) {
                info = map;
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new BusinessException("您没有创建联盟的权限");
        }
        //3、根据等级判断是否需要付费
        String itemValue = info.get("item_value").toString();//根据等级获取创建联盟的权限
        String[] arrs = itemValue.split(",");
        String isPay = arrs[0];
        if (isPay.equals("0")) {//不需要付费
            result.put("save", CommonConstant.COMMON_YES);//去创建联盟
        } else {
            //4、需要付费，判断是否已经付费
            UnionMainPermit unionMainPermit = this.unionMainPermitService.getByBusId(busId);
            if (unionMainPermit == null) {
                result.put("pay", CommonConstant.COMMON_YES);//去支付
                List<Map> list = this.dictService.getUnionCreatePackage();
                result.put("payItems", list);
                return result;
            }
            if (DateTimeKit.laterThanNow(unionMainPermit.getValidity())) {//未过期
                UnionMainCreate unionMainCreate = this.getByBusIdAndPermitId(busId, unionMainPermit.getId());
                if (unionMainCreate != null) {
                    throw new BusinessException("联盟未过期，不可创建");
                } else {
                    result.put("save", CommonConstant.COMMON_YES);//去创建联盟
                    result.put("isUsePermit", CommonConstant.COMMON_YES);
                    result.put("permitId", unionMainPermit.getId());
                }
            } else {//过期了
                result.put("pay", CommonConstant.COMMON_YES);//去支付
                List<Map> list = dictService.getUnionCreatePackage();
                result.put("payItems", list);
            }
        }
        return result;
    }

    /**
     * 根据商家id和表单信息，保存新建的联盟信息
     *
     * @param busId             {not null} 商家id
     * @param unionMainCreateVO {not null} 新建的联盟id
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveInstanceByBusIdAndVo(Integer busId, UnionMainCreateVO unionMainCreateVO) throws Exception {
        if (busId == null || unionMainCreateVO == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)判断是否具有盟主服务许可
        Map<String, Object> instancePermitMap = checkInstancePermit(busId);
        if (instancePermitMap.get("save") == null || !instancePermitMap.get("save").toString().equals(CommonConstant.COMMON_YES)) {
            throw new BusinessException("没有盟主服务许可或许可已过期");
        }
        // (2)判断盟主服务许可ID是否可用，以及是否已用
        Object isUsePermit = instancePermitMap.get("isUsePermit");
        UnionMainPermit unionMainPermit = null;
        if (isUsePermit != null && isUsePermit.toString().equals(CommonConstant.COMMON_YES)) {
            if (isUsePermit == null) {
                throw new BusinessException("没有盟主服务许可");
            }
            Integer permitId = unionMainCreateVO.getPermitId();
            unionMainPermit = this.unionMainPermitService.getByBusIdAndId(busId, permitId);
            if (unionMainPermit == null) {
                throw new BusinessException("没有盟主服务许可ID或已无效");
            }
            if (!DateTimeKit.laterThanNow(unionMainPermit.getValidity())) {
                throw new BusinessException("盟主服务许可已过期");
            }
            UnionMainCreate unionMainCreate = this.getByBusIdAndPermitId(busId, permitId);
            if (unionMainCreate != null) {
                throw new BusinessException("盟主服务许可已使用过，无法再次使用");
            }
        }
        // (3)保存的联盟信息
        UnionMain saveUnionMain = new UnionMain();
        UnionMainVO unionMainVO = unionMainCreateVO.getUnionMainVO();
        saveUnionMain.setCreatetime(DateUtil.getCurrentDate()); //创建时间
        saveUnionMain.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
        saveUnionMain.setName(unionMainVO.getUnionName()); //联盟名称
        String unionImg = unionMainVO.getUnionImg();
        if (unionImg.indexOf("/upload/") > -1) {
            saveUnionMain.setImg(unionImg.split("/upload/")[1]); //联盟图标
        }
        saveUnionMain.setJoinType(unionMainVO.getJoinType()); //联盟加盟方式
        saveUnionMain.setIsIntegral(unionMainVO.getIsIntegral()); //是否开启积分
        List<Map> dictList = this.dictService.getCreateUnionDict();
        BusUser busUser = this.busUserService.getBusUserById(busId);
        for (Map dict : dictList) {
            if (dict.get("item_key").equals(busUser.getLevel())) {
                String itemValue = dict.get("item_value").toString();
                String unionMember = itemValue.split(",")[1];
                saveUnionMain.setLimitMember(Integer.valueOf(unionMember)); //联盟成员总数上限
                break;
            }
        }
        if (unionMainPermit != null) {
            saveUnionMain.setUnionValidity(unionMainPermit.getValidity()); //联盟有效期
        } else {
            saveUnionMain.setUnionValidity(DateUtil.parseDate(CommonConstant.UNION_VALIDITY_DEFAULT)); //默认联盟有效期
        }
        // (4)联盟黑卡设置
        UnionMainChargeVO unionMainChargeVO = unionMainVO.getUnionMainChargeVO();
        UnionMainCharge blackUnionMainCharge = new UnionMainCharge();
        blackUnionMainCharge.setCreatetime(DateUtil.getCurrentDate()); //创建时间
        blackUnionMainCharge.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
        blackUnionMainCharge.setType(MainConstant.CHARGE_TYPE_BLACK); //红黑卡类型
        blackUnionMainCharge.setIsAvailable(unionMainChargeVO.getBlackIsAvailable()); //是否启用黑卡
        blackUnionMainCharge.setIsOldCharge(unionMainChargeVO.getBlackIsOldCharge()); //黑卡旧会员是否收费
        Integer blackIsCharge = unionMainChargeVO.getBlackIsCharge();
        blackUnionMainCharge.setIsCharge(blackIsCharge); //黑卡是否收费
        if (MainConstant.CHARGE_IS_CHARGE_YES == blackIsCharge) { //收费
            Double blackChargePrice = unionMainChargeVO.getBlackChargePrice();
            if (blackChargePrice < 0) {
                throw new BusinessException("黑卡价格不能小于0");
            }
            blackUnionMainCharge.setChargePrice(unionMainChargeVO.getBlackChargePrice()); //黑卡收费价格
            Integer blackValidityDay = unionMainChargeVO.getBlackValidityDay();
            if (blackValidityDay < 0) {
                throw new BusinessException("黑卡有效期不能小于0");
            }
            blackUnionMainCharge.setValidityDay(blackValidityDay); //黑卡有效期
            blackUnionMainCharge.setIllustration(unionMainChargeVO.getBlackIllustration()); //黑卡说明
        } else { //不收费
            blackUnionMainCharge.setChargePrice(0D); //黑卡收费价格
            blackUnionMainCharge.setValidityDay(0); //黑卡有效期
            blackUnionMainCharge.setIllustration(""); //黑卡说明
        }
        // (5)联盟红卡设置
        UnionMainCharge redUnionMainCharge = new UnionMainCharge();
        redUnionMainCharge.setCreatetime(DateUtil.getCurrentDate()); //创建时间
        redUnionMainCharge.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
        redUnionMainCharge.setType(MainConstant.CHARGE_TYPE_RED); //红黑卡类型
        redUnionMainCharge.setIsAvailable(unionMainChargeVO.getRedIsAvailable()); //是否启用红卡
        redUnionMainCharge.setIsOldCharge(unionMainChargeVO.getRedIsOldCharge()); //红卡旧会员是否收费
        Integer redIsCharge = unionMainChargeVO.getRedIsCharge();
        redUnionMainCharge.setIsCharge(redIsCharge); //红卡是否收费
        if (MainConstant.CHARGE_IS_CHARGE_YES == redIsCharge) { //收费
            Double redChargePrice = unionMainChargeVO.getRedChargePrice();
            if (redChargePrice < 0) {
                throw new BusinessException("红卡价格不能小于0");
            }
            redUnionMainCharge.setChargePrice(unionMainChargeVO.getRedChargePrice()); //红卡收费价格
            Integer redValidityDay = unionMainChargeVO.getRedValidityDay();
            if (redValidityDay < 0) {
                throw new BusinessException("红卡有效期不能小于0");
            }
            redUnionMainCharge.setValidityDay(redValidityDay); //红卡有效期
            redUnionMainCharge.setIllustration(unionMainChargeVO.getRedIllustration()); //红卡说明
        } else { //不收费
            redUnionMainCharge.setChargePrice(0D); //红卡收费价格
            redUnionMainCharge.setValidityDay(0); //红卡有效期
            redUnionMainCharge.setIllustration(""); //红卡说明
        }
        // (6)如果红、黑卡都启用的话，黑卡价格不能高于红卡价格
        if (MainConstant.CHARGE_IS_AVAILABLE_YES == blackUnionMainCharge.getIsAvailable()
                && MainConstant.CHARGE_IS_CHARGE_YES == blackIsCharge
                && MainConstant.CHARGE_IS_AVAILABLE_YES == redUnionMainCharge.getIsAvailable()
                && MainConstant.CHARGE_IS_CHARGE_YES == redIsCharge) {
            if (blackUnionMainCharge.getChargePrice() > redUnionMainCharge.getChargePrice()) {
                throw new BusinessException("黑卡价格不能高于红卡价格");
            }
        }
        // (7)联盟设置申请填写信息
        List<UnionMainDict> saveUnionMainDictList = new ArrayList<>();
        List<UnionMainDict> unionMainDictList = unionMainVO.getUnionMainDictList();
        if (ListUtil.isNotEmpty(unionMainDictList)) {
            for (UnionMainDict unionMainDict : unionMainDictList) {
                if (StringUtil.isNotEmpty(unionMainDict.getItemKey())) {
                    saveUnionMainDictList.add(unionMainDict);
                }
            }
        }
        // (7)盟主盟员信息
        UnionMemberVO unionMemberVO = unionMainCreateVO.getUnionMemberVO();
        UnionMember saveUnionOwner = new UnionMember();
        saveUnionOwner.setCreatetime(DateUtil.getCurrentDate()); //创建时间
        saveUnionOwner.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
        saveUnionOwner.setBusId(busId); //商家id
        saveUnionOwner.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_YES); //是否是盟主
        saveUnionOwner.setStatus(MemberConstant.STATUS_IN); //状态，已入盟
        saveUnionOwner.setEnterpriseName(unionMemberVO.getEnterpriseName()); //企业名称
        saveUnionOwner.setEnterpriseAddress(unionMemberVO.getEnterpriseAddress()); //企业地址
        saveUnionOwner.setDirectorName(unionMemberVO.getDirectorName()); //负责人名称
        saveUnionOwner.setDirectorPhone(unionMemberVO.getDirectorPhone()); //负责人电话
        saveUnionOwner.setDirectorEmail(unionMemberVO.getDirectorEmail()); //负责人邮件
        saveUnionOwner.setAddressLongitude(unionMemberVO.getAddressLongitude()); //地址经度
        saveUnionOwner.setAddressLatitude(unionMemberVO.getAddressLatitude()); //地址纬度
        saveUnionOwner.setAddressProvinceCode(unionMemberVO.getAddressProvinceCode()); //地址省份代码
        saveUnionOwner.setAddressCityCode(unionMemberVO.getAddressCityCode()); //地址城市代码
        saveUnionOwner.setAddressDistrictCode(unionMemberVO.getAddressDistrictCode()); //地址区代码
        saveUnionOwner.setNotifyPhone(unionMemberVO.getNotifyPhone()); //短信通知手机号
        saveUnionOwner.setIntegralExchangePercent(unionMemberVO.getIntegralExchangePercent()); //积分兑换率
        // (8)事务化处理
        //保存联盟
        this.unionMainService.insert(saveUnionMain);
        //保存黑卡设置
        blackUnionMainCharge.setUnionId(saveUnionMain.getId());
        this.unionMainChargeService.insert(blackUnionMainCharge);
        //保存红卡设置
        redUnionMainCharge.setUnionId(saveUnionMain.getId());
        this.unionMainChargeService.insert(redUnionMainCharge);
        //保存联盟设置申请填写信息
        if (ListUtil.isNotEmpty(saveUnionMainDictList)) {
            for (UnionMainDict unionMainDict : unionMainDictList) {
                unionMainDict.setUnionId(saveUnionMain.getId());
            }
            this.unionMainDictService.insertBatch(saveUnionMainDictList);
        }
        //保存盟主信息
        saveUnionOwner.setUnionId(saveUnionMain.getId());
        this.unionMemberService.insert(saveUnionOwner);
    }

    /**
     * 根据商家id和服务许可id，获取联盟创建信息
     *
     * @param busId    {not null} 商家id
     * @param permitId {not null} 联盟服务许可
     * @return
     * @throws Exception
     */
    @Override
    public UnionMainCreate getByBusIdAndPermitId(Integer busId, Integer permitId) throws Exception {
        if (busId == null || permitId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("permit_id", permitId);
        return this.selectOne(entityWrapper);
    }
}
