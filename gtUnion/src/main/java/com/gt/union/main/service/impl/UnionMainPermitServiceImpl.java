package com.gt.union.main.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.main.constant.MainConstant;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.entity.UnionMainPermit;
import com.gt.union.main.mapper.UnionMainPermitMapper;
import com.gt.union.main.service.IUnionMainPermitService;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.setting.entity.UnionSettingMainCharge;
import com.gt.union.setting.service.IUnionSettingMainChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联盟许可，盟主服务 服务实现类
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
@Service
public class UnionMainPermitServiceImpl extends ServiceImpl<UnionMainPermitMapper, UnionMainPermit> implements IUnionMainPermitService {
    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private IDictService dictService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionSettingMainChargeService unionSettingMainChargeService;

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    @Override
    public UnionMainPermit getByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        return this.selectOne(entityWrapper);
    }

    @Override
    public UnionMainPermit getByBusIdAndId(Integer busId, Integer id) throws Exception {
        if (busId == null || id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("id", id);
        return this.selectOne(entityWrapper);
    }

    /**
     * 获取免费的盟主权限
     */
    private Map<Integer, Object> getFreeUnionMainAuthority() {
        List<Map> list = dictService.getCreateUnionDict();
        Map<Integer, Object> data = new HashMap<>(16);
        for (Map map : list) {
            String payment = map.get("item_value").toString().split(",")[0];
            if ("".equals(payment)) {
                data.put(CommonUtil.toInteger(map.get("item_key")), 1);
            }
        }
        return data;
    }

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    @Override
    public List<UnionMainPermit> listExpired() throws Exception {
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .lt("validity", DateUtil.getCurrentDate());
        return this.selectList(entityWrapper);
    }

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    @Override
    public boolean hasUnionMainPermit(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        BusUser busUser = this.busUserService.getBusUserById(busId);
        if (busUser == null) {
            throw new ParamException("商家帐号不存在");
        }
        //（1）判断商家版本：至尊版、白金版、钻石版直接拥有盟主服务许可
        Map<Integer, Object> data = getFreeUnionMainAuthority();
        if (data.containsKey(busUser.getLevel())) {
            return true;
        }
        //（2）其他版本，如升级版、高级版需要判断是否购买了盟主服务
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .orderBy("id", false);
        UnionMainPermit unionMainPermit = this.selectOne(entityWrapper);
        return unionMainPermit != null && DateUtil.getCurrentDate().compareTo(unionMainPermit.getValidity()) < 0;
    }

    @Override
    public Map<String, Object> createUnionQRCode(BusUser user, Integer chargeId) throws Exception {
        Map<String, Object> data = new HashMap<>(16);
        UnionSettingMainCharge charge = unionSettingMainChargeService.getById(chargeId);
        if (charge == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Double pay = charge.getMoney();
        String orderNo = ConfigConstant.CREATE_UNION_PAY_ORDER_CODE + System.currentTimeMillis();
        String only = DateTimeKit.getDateTime(new Date(), DateTimeKit.yyyyMMddHHmmss);
        WxPublicUsers publicUser = busUserService.getWxPublicUserByBusId(PropertiesUtil.getDuofenBusId());
        data.put("totalFee", pay);
        data.put("busId", PropertiesUtil.getDuofenBusId());
        //是否墨盒支付
        data.put("sourceType", 1);
        //系统判断支付方式
        data.put("payWay", 0);
        //0：不需要同步跳转
        data.put("isreturn", 0);
        data.put("model", ConfigConstant.PAY_MODEL);
        data.put("notifyUrl", PropertiesUtil.getUnionUrl() + "/unionMainPermit/79B4DE7C/paymentSuccess/" + only);
        //订单号
        data.put("orderNum", orderNo);
        //支付的商家id
        data.put("payBusId", user.getId());
        //不推送
        data.put("isSendMessage", 0);
        //appid
        data.put("appid", publicUser.getAppid());
        data.put("desc", "多粉-创建联盟");
        //公众号
        data.put("appidType", 0);
        data.put("only", only);
        data.put("infoItemKey", chargeId);
        String paramKey = RedisKeyUtil.getCreateUnionPayParamKey(only);
        String statusKey = RedisKeyUtil.getCreateUnionPayStatusKey(only);
        //5分钟
        redisCacheUtil.set(paramKey, data, 360L);
        //等待扫码状态
        redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_001, 300L);
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payCreateUnionSuccess(String orderNo, String only, Integer payType) throws Exception {
        String paramKey = RedisKeyUtil.getCreateUnionPayParamKey(only);
        String obj = redisCacheUtil.get(paramKey);
        if (CommonUtil.isEmpty(obj)) {
            throw new BusinessException("订单不存在或超时");
        }
        Map<String, Object> result = JSONObject.parseObject(obj, Map.class);
        String statusKey = RedisKeyUtil.getCreateUnionPayStatusKey(only);
        //判断订单是否支付
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("sys_order_no", orderNo);
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        entityWrapper.eq("order_status", MainConstant.PERMIT_ORDER_STATUS_SUCCESS);
        UnionMainPermit mainPermit = this.selectOne(entityWrapper);
        if (mainPermit != null) {
            throw new BusinessException("该订单已支付");
        }
        //添加支付订单
        Integer busId = CommonUtil.toInteger(result.get("payBusId"));
        Integer chargeId = CommonUtil.toInteger(result.get("infoItemKey"));
        UnionSettingMainCharge charge = unionSettingMainChargeService.getById(chargeId);
        int month = (int) (new BigDecimal(charge.getYear()).multiply(new BigDecimal(12)).doubleValue());
        //有效期
        Date validity = DateTimeKit.addMonths(month);
        //判断该商家是否有许可  新增或更新许可
        UnionMainPermit myPermit = this.getByBusId(busId);
        if (myPermit != null) {
            UnionMainPermit unionMainPermit = new UnionMainPermit();
            unionMainPermit.setId(myPermit.getId());
            unionMainPermit.setDelStatus(CommonConstant.DEL_STATUS_YES);
            this.updateById(unionMainPermit);
        }
        UnionMainPermit unionMainPermit = new UnionMainPermit();
        unionMainPermit.setBusId(busId);
        unionMainPermit.setOrderMoney(CommonUtil.toDouble(result.get("totalFee")));
        unionMainPermit.setDelStatus(CommonConstant.DEL_STATUS_NO);
        unionMainPermit.setCreatetime(new Date());
        unionMainPermit.setOrderDesc(result.get("desc").toString());
        unionMainPermit.setSysOrderNo(orderNo);
        unionMainPermit.setOrderStatus(MainConstant.PERMIT_ORDER_STATUS_UNPAY);
        unionMainPermit.setValidity(validity);
        //payType 0：微信支付 1：支付宝支付   联盟保存类型：1：微信支付 3：支付宝支付
        unionMainPermit.setPayType(payType == 0 ? 1 : payType == 1 ? 3 : 0);
        this.insert(unionMainPermit);

        UnionMember member = unionMemberService.getOwnerByBusId(busId);
        if (member != null) {
            UnionMain main = unionMainService.getById(member.getUnionId());
            if (main != null) {
                //将联盟有效期续期
                UnionMain unionMain = new UnionMain();
                unionMain.setId(main.getId());
                unionMain.setUnionValidity(validity);
                unionMainService.updateById(unionMain);
            }
        }
        redisCacheUtil.remove(paramKey);
        //支付成功
        redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_003, 60L);
    }

    //******************************************* Object As a Service - get ********************************************

    //******************************************* Object As a Service - list *******************************************

    //******************************************* Object As a Service - save *******************************************

    //******************************************* Object As a Service - remove *****************************************

    //******************************************* Object As a Service - update *****************************************

    //***************************************** Object As a Service - cache support ************************************
}
