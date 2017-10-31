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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 创建联盟 服务实现类
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
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

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    @Override
    public Map<String, Object> instanceByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        return checkInstancePermit(busId);
    }

    /**
     * 检查盟主服务许可
     */
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
        //创建联盟的权限
        List<Map> createDict = dictService.getCreateUnionDict();
        boolean flag = false;
        Map info = null;
        Integer itemKey;
        for (Map map : createDict) {
            itemKey = CommonUtil.toInteger(map.get("item_key"));
            if (itemKey != null && itemKey.equals(busUser.getLevel())) {
                info = map;
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new BusinessException("您没有联盟权限");
        }
        //3、判断是否有盟主权限
        //根据等级获取创建联盟的权限
        String itemValue = info.get("item_value").toString();
        String[] arrs = itemValue.split(",");
        //是否有创建盟主的服务权限 1：是 0：否
        String isUnionOwnerService = arrs[0];
        //创建联盟是否需要付费  1：需要  0：不需要
        String isPay = arrs[1];
        //商家的联盟版本  盟主版、盟员版、无
        String unionLevelDesc = arrs[2];
        if ("0".equals(isUnionOwnerService)) {
            throw new BusinessException("您没有创建联盟的权限");
        }
        //4、判断是否有盟主许可
        UnionMainPermit permit = this.unionMainPermitService.getByBusId(busId);
        if (permit != null) {
            if (DateTimeKit.laterThanNow(permit.getValidity())) {
                //许可没过期
                if ("0".equals(isPay)) {
                    //不需要付费
                    result.put("isPay", isPay);
                    //去创建联盟
                    result.put("save", CommonConstant.COMMON_YES);
                    result.put("permitId", permit.getId());
                    return result;
                } else {
                    if (permit.getValidity().getTime() == DateUtil.parseDate(CommonConstant.UNION_VALIDITY_DEFAULT).getTime() && permit.getOrderMoney() == 0) {
                        //免费创建的 需付费 去支付
                        result.put("pay", CommonConstant.COMMON_YES);
                        List<UnionSettingMainCharge> list = unionSettingMainChargeService.listBusLevel(busUser.getLevel());
                        result.put("payItems", list);
                        //联盟成员数
                        result.put("number", list.get(0).getNumber());
                        String levelDesc = dictService.getBusUserLevel(busUser.getLevel());
                        result.put("levelDesc", levelDesc);
                        result.put("unionLevelDesc", unionLevelDesc);
                        return result;
                    } else {
                        UnionMainCreate unionMainCreate = this.getByBusIdAndPermitId(busId, permit.getId());
                        if (unionMainCreate != null) {
                            throw new BusinessException("盟主服务许可已使用过，无法再次使用");
                        } else {
                            result.put("isPay", isPay);
                            //去创建联盟
                            result.put("save", CommonConstant.COMMON_YES);
                            result.put("permitId", permit.getId());
                            return result;
                        }
                    }
                }
            } else {//过期
                if ("0".equals(isPay)) {
                    //不需要付费
                    result.put("isPay", isPay);
                    //去创建联盟
                    result.put("save", CommonConstant.COMMON_YES);
                    result.put("permitId", permit.getId());
                    return result;
                } else {
                    //去支付
                    result.put("pay", CommonConstant.COMMON_YES);
                    List<UnionSettingMainCharge> list = unionSettingMainChargeService.listBusLevel(busUser.getLevel());
                    result.put("payItems", list);
                    //联盟成员数
                    result.put("number", list.get(0).getNumber());
                    String levelDesc = dictService.getBusUserLevel(busUser.getLevel());
                    result.put("levelDesc", levelDesc);
                    result.put("unionLevelDesc", unionLevelDesc);
                    return result;
                }
            }
        } else {
            if ("0".equals(isPay)) {//不需要付费 创建许可
                List<UnionSettingMainCharge> list = unionSettingMainChargeService.listBusLevel(busUser.getLevel());
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
                unionMainPermit.setSettingMainChargeId(list.get(0).getId());
                unionMainPermitService.insert(unionMainPermit);
                result.put("isPay", isPay);
                //去创建联盟
                result.put("save", CommonConstant.COMMON_YES);
                result.put("permitId", unionMainPermit.getId());
                return result;
            } else {
                //付费 创建许可 去支付
                result.put("pay", CommonConstant.COMMON_YES);
                List<UnionSettingMainCharge> list = unionSettingMainChargeService.listBusLevel(busUser.getLevel());
                result.put("payItems", list);
                //联盟成员数
                result.put("number", list.get(0).getNumber());
                String levelDesc = dictService.getBusUserLevel(busUser.getLevel());
                result.put("levelDesc", levelDesc);
                result.put("unionLevelDesc", unionLevelDesc);
                return result;
            }
        }
    }

    @Override
    public UnionMainCreate getByBusIdAndPermitId(Integer busId, Integer permitId) throws Exception {
        if (busId == null || permitId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("permit_id", permitId);
        return this.selectOne(entityWrapper);
    }

    @Override
    public UnionMainCreate getByPermitId(Integer permitId) throws Exception {
        if (permitId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("permit_id", permitId)
                .orderBy("id", false);
        return this.selectOne(entityWrapper);
    }

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    @Override
    public List<UnionMainCreate> listExpired() throws Exception {
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .isNotNull("permit_id")
                .notExists(" SELECT p.id FROM t_union_main_permit p"
                        + " WHERE p.del_status = " + CommonConstant.DEL_STATUS_NO
                        + "  AND p.id = t_union_main_create.permit_id");
        return this.selectList(entityWrapper);
    }

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveInstanceByBusIdAndVo(Integer busId, UnionMainCreateVO unionMainCreateVO) throws Exception {
        if (busId == null || unionMainCreateVO == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)判断是否具有盟主服务许可
        Map<String, Object> instancePermitMap = checkInstancePermit(busId);
        Integer permitId = unionMainCreateVO.getPermitId();
        UnionMainPermit unionMainPermit = this.unionMainPermitService.getByBusIdAndId(busId, permitId);
        if (instancePermitMap.get("save") == null || !instancePermitMap.get("save").toString().equals(String.valueOf(CommonConstant.COMMON_YES)) || unionMainPermit == null) {
            throw new BusinessException("没有盟主服务许可或许可已过期");
        }
        //是否需要支付
        Integer isPay = CommonUtil.toInteger(instancePermitMap.get("isPay"));
        // (2)保存的联盟信息
        UnionMain saveUnionMain = new UnionMain();
        UnionMainVO unionMainVO = unionMainCreateVO.getUnionMainVO();
        //创建时间
        saveUnionMain.setCreatetime(DateUtil.getCurrentDate());
        //删除状态
        saveUnionMain.setDelStatus(CommonConstant.DEL_STATUS_NO);
        //联盟名称
        saveUnionMain.setName(unionMainVO.getUnionName());
        //联盟图标
        saveUnionMain.setImg(unionMainVO.getUnionImg());
        //联盟加盟方式
        saveUnionMain.setJoinType(unionMainVO.getJoinType());
        //是否开启积分
        saveUnionMain.setIsIntegral(unionMainVO.getIsIntegral());
        //盟主收费设置
        Integer settingMainChargeId = unionMainPermit.getSettingMainChargeId();
        UnionSettingMainCharge settingMainCharge = unionSettingMainChargeService.getById(settingMainChargeId);
        Integer limitMember = settingMainCharge.getNumber();
        //联盟成员总数上限
        saveUnionMain.setLimitMember(limitMember);
        //联盟有效期
        saveUnionMain.setUnionValidity(isPay != null && isPay == 1 ? unionMainPermit.getValidity() : DateUtil.parseDate(CommonConstant.UNION_VALIDITY_DEFAULT));
        // (3)联盟黑卡设置
        UnionMainChargeVO unionMainChargeVO = unionMainVO.getUnionMainChargeVO();
        UnionMainCharge blackUnionMainCharge = new UnionMainCharge();
        //创建时间
        blackUnionMainCharge.setCreatetime(DateUtil.getCurrentDate());
        //删除状态
        blackUnionMainCharge.setDelStatus(CommonConstant.DEL_STATUS_NO);
        //红黑卡类型
        blackUnionMainCharge.setType(MainConstant.CHARGE_TYPE_BLACK);
        //是否启用黑卡
        blackUnionMainCharge.setIsAvailable(unionMainChargeVO.getBlackIsAvailable());
        //黑卡旧会员是否收费
        blackUnionMainCharge.setIsOldCharge(unionMainChargeVO.getBlackIsOldCharge());
        Integer blackIsCharge = unionMainChargeVO.getBlackIsCharge();
        //黑卡是否收费
        blackUnionMainCharge.setIsCharge(blackIsCharge);
        if (MainConstant.CHARGE_IS_CHARGE_YES == blackIsCharge) {
            //收费
            Double blackChargePrice = unionMainChargeVO.getBlackChargePrice();
            if (blackChargePrice < 0) {
                throw new BusinessException("黑卡价格不能小于0");
            }
            //黑卡收费价格
            blackUnionMainCharge.setChargePrice(unionMainChargeVO.getBlackChargePrice());
            Integer blackValidityDay = unionMainChargeVO.getBlackValidityDay();
            if (blackValidityDay < 0) {
                throw new BusinessException("黑卡有效期不能小于0");
            }
            //黑卡有效期
            blackUnionMainCharge.setValidityDay(blackValidityDay);
            //黑卡说明
            blackUnionMainCharge.setIllustration(unionMainChargeVO.getBlackIllustration());
        } else {
            //不收费
            //黑卡收费价格
            blackUnionMainCharge.setChargePrice(0D);
            //黑卡有效期
            blackUnionMainCharge.setValidityDay(0);
            //黑卡说明
            blackUnionMainCharge.setIllustration("");
        }
        // (4)联盟红卡设置
        UnionMainCharge redUnionMainCharge = new UnionMainCharge();
        //创建时间
        redUnionMainCharge.setCreatetime(DateUtil.getCurrentDate());
        //删除状态
        redUnionMainCharge.setDelStatus(CommonConstant.DEL_STATUS_NO);
        //红黑卡类型
        redUnionMainCharge.setType(MainConstant.CHARGE_TYPE_RED);
        //是否启用红卡
        redUnionMainCharge.setIsAvailable(unionMainChargeVO.getRedIsAvailable());
        //红卡旧会员是否收费
        redUnionMainCharge.setIsOldCharge(unionMainChargeVO.getRedIsOldCharge());
        Integer redIsCharge = unionMainChargeVO.getRedIsCharge();
        //红卡是否收费
        redUnionMainCharge.setIsCharge(redIsCharge);
        if (MainConstant.CHARGE_IS_CHARGE_YES == redIsCharge) {
            //收费
            Double redChargePrice = unionMainChargeVO.getRedChargePrice();
            if (redChargePrice < 0) {
                throw new BusinessException("红卡价格不能小于0");
            }
            //红卡收费价格
            redUnionMainCharge.setChargePrice(unionMainChargeVO.getRedChargePrice());
            Integer redValidityDay = unionMainChargeVO.getRedValidityDay();
            if (redValidityDay < 0) {
                throw new BusinessException("红卡有效期不能小于0");
            }
            //红卡有效期
            redUnionMainCharge.setValidityDay(redValidityDay);
            //红卡说明
            redUnionMainCharge.setIllustration(unionMainChargeVO.getRedIllustration());
        } else {
            //不收费
            //红卡收费价格
            redUnionMainCharge.setChargePrice(0D);
            //红卡有效期
            redUnionMainCharge.setValidityDay(0);
            //红卡说明
            redUnionMainCharge.setIllustration("");
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
        //创建时间
        saveUnionOwner.setCreatetime(DateUtil.getCurrentDate());
        //删除状态
        saveUnionOwner.setDelStatus(CommonConstant.DEL_STATUS_NO);
        //商家id
        saveUnionOwner.setBusId(busId);
        //是否是盟主
        saveUnionOwner.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_YES);
        //状态，已入盟
        saveUnionOwner.setStatus(MemberConstant.STATUS_IN);
        //企业名称
        saveUnionOwner.setEnterpriseName(unionMemberVO.getEnterpriseName());
        //企业地址
        saveUnionOwner.setEnterpriseAddress(unionMemberVO.getEnterpriseAddress());
        //负责人名称
        saveUnionOwner.setDirectorName(unionMemberVO.getDirectorName());
        //负责人电话
        saveUnionOwner.setDirectorPhone(unionMemberVO.getDirectorPhone());
        //负责人邮件
        saveUnionOwner.setDirectorEmail(unionMemberVO.getDirectorEmail());
        //地址经度
        saveUnionOwner.setAddressLongitude(unionMemberVO.getAddressLongitude());
        //地址纬度
        saveUnionOwner.setAddressLatitude(unionMemberVO.getAddressLatitude());
        //地址省份代码
        saveUnionOwner.setAddressProvinceCode(unionMemberVO.getAddressProvinceCode());
        //地址城市代码
        saveUnionOwner.setAddressCityCode(unionMemberVO.getAddressCityCode());
        //地址区代码
        saveUnionOwner.setAddressDistrictCode(unionMemberVO.getAddressDistrictCode());
        //短信通知手机号
        saveUnionOwner.setNotifyPhone(unionMemberVO.getNotifyPhone());
        //积分兑换率
        saveUnionOwner.setIntegralExchangePercent(unionMemberVO.getIntegralExchangePercent());
        // (8)事务化处理
        //保存联盟
        this.unionMainService.save(saveUnionMain);
        //保存黑卡设置
        blackUnionMainCharge.setUnionId(saveUnionMain.getId());
        this.unionMainChargeService.save(blackUnionMainCharge);
        //保存红卡设置
        redUnionMainCharge.setUnionId(saveUnionMain.getId());
        this.unionMainChargeService.save(redUnionMainCharge);
        //保存联盟设置申请填写信息
        if (ListUtil.isNotEmpty(saveUnionMainDictList)) {
            for (UnionMainDict unionMainDict : unionMainDictList) {
                unionMainDict.setUnionId(saveUnionMain.getId());
            }
            this.unionMainDictService.saveBatch(saveUnionMainDictList);
        }
        //保存盟主信息
        saveUnionOwner.setUnionId(saveUnionMain.getId());
        this.unionMemberService.save(saveUnionOwner);
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

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    @Override
    public boolean hasCreateUnionMain(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        UnionMainCreate unionMainCreate = this.selectOne(entityWrapper);
        return unionMainCreate != null;
    }

    //******************************************* Object As a Service - get ********************************************

    //******************************************* Object As a Service - list *******************************************

    //******************************************* Object As a Service - save *******************************************

    //******************************************* Object As a Service - remove *****************************************

    //******************************************* Object As a Service - update *****************************************

    //***************************************** Object As a Service - cache support ************************************
}
