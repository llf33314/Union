package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.api.bean.session.BusUser;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
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
import com.gt.union.setting.entity.UnionSettingMainCharge;
import com.gt.union.setting.service.IUnionSettingMainChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
    private IUnionSettingMainChargeService unionSettingMainChargeService;

    //-------------------------------------------------- get ----------------------------------------------------------

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
        BusUser busUser = this.busUserService.getBusUserById(busId);
        if (busUser == null) {
            throw new BusinessException("账号不存在");
        }
        //2、判断是否有联盟权限
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
            throw new BusinessException("您没有联盟权限");
        }
        //3、判断是否有盟主权限
        String itemValue = info.get("item_value").toString();//根据等级获取创建联盟的权限
        String[] arrs = itemValue.split(",");
        String isUnionOwnerService = arrs[0];//是否有创建盟主的服务权限 1：是 0：否
        String isPay = arrs[1];//创建联盟是否需要付费  1：需要  0：不需要
        if (isUnionOwnerService.equals("0")) {
            throw new BusinessException("您没有创建联盟的权限");
        }
        //4、判断是否有盟主许可
        UnionMainPermit permit = this.unionMainPermitService.getByBusId(busId);
        if(permit != null){
            if(DateTimeKit.laterThanNow(permit.getValidity())){//许可没过期
                if(isPay.equals("0")){//不需要付费
                    result.put("isPay",isPay);
                    result.put("save", CommonConstant.COMMON_YES);//去创建联盟
                    result.put("permitId", permit.getId());
                    return result;
                }else {
                    if(permit.getValidity().getTime() == DateUtil.parseDate(CommonConstant.UNION_VALIDITY_DEFAULT).getTime() && permit.getOrderMoney().equals(0)){//免费创建的
                        //需付费
                        result.put("pay", CommonConstant.COMMON_YES);//去支付
                        List<UnionSettingMainCharge> list =unionSettingMainChargeService.listBusLevel(busUser.getLevel());
                        result.put("payItems", list);
                        result.put("number",list.get(0).getNumber());//联盟成员数
                        String levelDesc = dictService.getBusUserLevel(busUser.getLevel());
                        result.put("levelDesc", levelDesc);
                        return result;
                    }else {
                        UnionMainCreate unionMainCreate = this.getByBusIdAndPermitId(busId, permit.getId());
                        if(unionMainCreate != null){
                            throw new BusinessException("盟主服务许可已使用过，无法再次使用");
                        }else {
                            result.put("isPay",isPay);
                            result.put("save", CommonConstant.COMMON_YES);//去创建联盟
                            result.put("permitId", permit.getId());
                            return result;
                        }
                    }
                }
            }else {//过期
                if(isPay.equals("0")){//不需要付费
                    result.put("isPay",isPay);
                    result.put("save", CommonConstant.COMMON_YES);//去创建联盟
                    result.put("permitId", permit.getId());
                    return result;
                }else {
                    result.put("pay", CommonConstant.COMMON_YES);//去支付
                    List<UnionSettingMainCharge> list =unionSettingMainChargeService.listBusLevel(busUser.getLevel());
                    result.put("payItems", list);
                    result.put("number",list.get(0).getNumber());//联盟成员数
                    String levelDesc = dictService.getBusUserLevel(busUser.getLevel());
                    result.put("levelDesc", levelDesc);
                    return result;
                }
            }
        }else {
            if(isPay.equals("0")) {//不需要付费 创建许可
                String orderNo = ConfigConstant.CREATE_UNION_PAY_ORDER_CODE + System.currentTimeMillis();
                UnionMainPermit unionMainPermit = new UnionMainPermit();
                unionMainPermit.setBusId(busId);
                unionMainPermit.setOrderMoney(0d);
                unionMainPermit.setDelStatus(CommonConstant.DEL_STATUS_NO);
                unionMainPermit.setCreatetime(new Date());
                unionMainPermit.setOrderDesc("多粉-创建联盟免费");
                unionMainPermit.setSysOrderNo(orderNo);
                unionMainPermit.setOrderStatus(MainConstant.PERMIT_ORDER_STATUS_UNPAY);
                unionMainPermit.setValidity(DateUtil.parseDate(CommonConstant.UNION_VALIDITY_DEFAULT));//默认有效期
                unionMainPermitService.insert(unionMainPermit);
                result.put("isPay",isPay);
                result.put("save", CommonConstant.COMMON_YES);//去创建联盟
                result.put("permitId", unionMainPermit.getId());
                return result;
            }else {//付费 创建许可
                result.put("pay", CommonConstant.COMMON_YES);//去支付
                List<UnionSettingMainCharge> list =unionSettingMainChargeService.listBusLevel(busUser.getLevel());
                result.put("payItems", list);
                result.put("number",list.get(0).getNumber());//联盟成员数
                String levelDesc = dictService.getBusUserLevel(busUser.getLevel());
                result.put("levelDesc", levelDesc);
                return result;
            }
        }
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

    /**
     * 通过盟主服务许可id，获取联盟创建信息
     *
     * @param permitId {not null} 盟主服务许可id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMainCreate getByPermitId(Integer permitId) throws Exception {
        if (permitId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("permit_id", permitId)
                .orderBy("id", false);
        return this.selectOne(entityWrapper);
    }

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 获取所有过期的联盟创建列表记录，过期是指因盟主服务许可过期而导致的过期
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMainCreate> listExpired() throws Exception {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .isNotNull("permit_id")
                .notExists(new StringBuilder(" SELECT p.id FROM t_union_main_permit p")
                        .append(" WHERE p.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND p.id = t_union_main_create.permit_id")
                        .toString());
        return this.selectList(entityWrapper);
    }

    //------------------------------------------------- update --------------------------------------------------------
    //------------------------------------------------- save ----------------------------------------------------------

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
        Integer permitId = unionMainCreateVO.getPermitId();
        UnionMainPermit unionMainPermit = this.unionMainPermitService.getByBusIdAndId(busId, permitId);
        if (instancePermitMap.get("save") == null || !instancePermitMap.get("save").toString().equals(String.valueOf(CommonConstant.COMMON_YES)) || unionMainPermit == null){
			throw new BusinessException("没有盟主服务许可或许可已过期");
		}
		Integer isPay = CommonUtil.toInteger(instancePermitMap.get("isPay"));//是否需要支付
        // (2)保存的联盟信息
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
        Integer limitMember = this.unionMainService.getLimitMemberByBusId(busId);
        saveUnionMain.setLimitMember(limitMember); //联盟成员总数上限
		saveUnionMain.setUnionValidity(isPay == 1 ? unionMainPermit.getValidity() : DateUtil.parseDate(CommonConstant.UNION_VALIDITY_DEFAULT)); //联盟有效期
        // (3)联盟黑卡设置
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
        // (4)联盟红卡设置
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
        // (5)如果红、黑卡都启用的话，黑卡价格不能高于红卡价格
        if (MainConstant.CHARGE_IS_AVAILABLE_YES == blackUnionMainCharge.getIsAvailable()
                && MainConstant.CHARGE_IS_CHARGE_YES == blackIsCharge
                && MainConstant.CHARGE_IS_AVAILABLE_YES == redUnionMainCharge.getIsAvailable()
                && MainConstant.CHARGE_IS_CHARGE_YES == redIsCharge) {
            if (blackUnionMainCharge.getChargePrice() > redUnionMainCharge.getChargePrice()) {
                throw new BusinessException("黑卡价格不能高于红卡价格");
            }
        }
        // (6)联盟设置申请填写信息
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
        //保存创建联盟记录
        UnionMainCreate unionMainCreate = this.getByBusIdAndPermitId(busId, permitId);
        if (unionMainCreate != null) {
            UnionMainCreate delUnionMainCreate = new UnionMainCreate();
            delUnionMainCreate.setId(unionMainCreate.getId());
            delUnionMainCreate.setDelStatus(CommonConstant.DEL_STATUS_YES);
            this.updateById(delUnionMainCreate);
        }
        UnionMainCreate mainCreate = new UnionMainCreate();
        mainCreate.setBusId(busId);
        mainCreate.setCreatetime(new Date());
        mainCreate.setDelStatus(CommonConstant.DEL_STATUS_NO);
        mainCreate.setPermitId(unionMainPermit.getId());
        mainCreate.setUnionId(saveUnionMain.getId());
        this.insert(mainCreate);

    }

    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------

    /**
     * 根据商家id判断是否创建过联盟
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
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
}
