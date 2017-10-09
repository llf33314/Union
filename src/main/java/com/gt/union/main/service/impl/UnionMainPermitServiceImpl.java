package com.gt.union.main.service.impl;

import com.alibaba.fastjson.JSON;
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
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟许可，盟主服务 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
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

    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据商家id获取联盟服务许可
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMainPermit getByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据商家id和许可id获取联盟服务许可信息
     *
     * @param busId {not null} 商家id
     * @param id    {not null} 联盟许可id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMainPermit getByBusIdAndId(Integer busId, Integer id) throws Exception {
        if (busId == null || id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("id", id);
        return this.selectOne(entityWrapper);
    }

    /**
     * 获取免费的盟主权限
     *
     * @return
     */
    private Map<Integer, Object> getFreeUnionMainAuthority() {
        List<Map> list = dictService.getCreateUnionDict();
        Map<Integer, Object> data = new HashMap<Integer, Object>();
        for (Map map : list) {
            String payment = map.get("item_value").toString().split(",")[0];
            if (payment.equals("")) {
                data.put(CommonUtil.toInteger(map.get("item_key")), 1);
            }
        }
        return data;
    }

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 获取所有已过期的、但为未删除状态的盟主服务许哭列表信息
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMainPermit> listExpired() throws Exception {
        EntityWrapper entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .lt("validity", DateUtil.getCurrentDate());
        return this.selectList(entityWrapper);
    }

    //------------------------------------------------- update --------------------------------------------------------
    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------

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
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .orderBy("id", false);
        UnionMainPermit unionMainPermit = this.selectOne(entityWrapper);
        if (unionMainPermit != null && DateUtil.getCurrentDate().compareTo(unionMainPermit.getValidity()) < 0) {
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> createUnionQRCode(BusUser user, Integer chargeId) throws Exception{
        Map<String, Object> data = new HashMap<String, Object>();
        UnionSettingMainCharge charge = unionSettingMainChargeService.getById(chargeId);
        if(charge == null){
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Double pay = charge.getMoney();
        String orderNo = ConfigConstant.CREATE_UNION_PAY_ORDER_CODE + System.currentTimeMillis();
        String only= DateTimeKit.getDateTime(new Date(), DateTimeKit.yyyyMMddHHmmss);
        WxPublicUsers publicUser = busUserService.getWxPublicUserByBusId(ConfigConstant.WXMP_DUOFEN_BUSID);
        data.put("totalFee",pay);
        data.put("busId", ConfigConstant.WXMP_DUOFEN_BUSID);
        data.put("sourceType", 1);//是否墨盒支付
        data.put("payWay",0);//系统判断支付方式
        data.put("isreturn",0);//0：不需要同步跳转
        data.put("model", ConfigConstant.PAY_MODEL);
        data.put("notifyUrl", ConfigConstant.UNION_ROOT_URL + "/unionMainPermit/79B4DE7C/paymentSuccess/" + only);
        data.put("orderNum", orderNo);//订单号
        data.put("payBusId", user);//支付的商家id
        data.put("isSendMessage",0);//不推送
        data.put("appid",publicUser.getAppid());//appid
        data.put("desc", "多粉-创建联盟");
        data.put("appidType",0);//公众号
        data.put("only", only);
        data.put("infoItemKey", chargeId);
        String paramKey = RedisKeyUtil.getCreateUnionPayParamKey(only);
        String statusKey = RedisKeyUtil.getCreateUnionPayStatusKey(only);
        redisCacheUtil.set(paramKey, data, 360l);//5分钟
        redisCacheUtil.set(statusKey,ConfigConstant.USER_ORDER_STATUS_001,300l);//等待扫码状态
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payCreateUnionSuccess(String orderNo, String only) throws Exception{
        String paramKey = RedisKeyUtil.getCreateUnionPayParamKey(only);
        String obj = redisCacheUtil.get(paramKey);
        if(CommonUtil.isEmpty(obj)){
            throw new BusinessException("订单不存在或超时");
        }
        Map<String,Object> result = JSONObject.parseObject(obj,Map.class);
        String statusKey = RedisKeyUtil.getCreateUnionPayStatusKey(only);
        //判断订单是否支付
        EntityWrapper entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("sys_order_no",orderNo);
        entityWrapper.eq("del_status",CommonConstant.DEL_STATUS_NO);
        entityWrapper.eq("order_status",MainConstant.PERMIT_ORDER_STATUS_SUCCESS);
        UnionMainPermit mainPermit = this.selectOne(entityWrapper);
        if(mainPermit != null){
            throw new BusinessException("该订单已支付");
        }
        //添加支付订单
        Integer busId = CommonUtil.toInteger(result.get("payBusId"));
        Integer chargeId = CommonUtil.toInteger(result.get("infoItemKey"));
        UnionSettingMainCharge charge = unionSettingMainChargeService.getById(chargeId);
        int month = (int)(new BigDecimal(charge.getYear()).multiply(new BigDecimal(12)).doubleValue());
        Date validity = DateTimeKit.addMonths(month);//有效期
        //判断该商家是否有许可  新增或更新许可
        UnionMainPermit myPermit = this.getByBusId(busId);
        if(myPermit != null){
            UnionMainPermit unionMainPermit = new UnionMainPermit();
            unionMainPermit.setId(mainPermit.getId());
            unionMainPermit.setDelStatus(CommonConstant.DEL_STATUS_YES);
            this.updateById(unionMainPermit);
        }
        UnionMainPermit unionMainPermit = new UnionMainPermit();
        unionMainPermit.setBusId(busId);
        unionMainPermit.setOrderMoney(0d);
        unionMainPermit.setDelStatus(CommonConstant.DEL_STATUS_NO);
        unionMainPermit.setCreatetime(new Date());
        unionMainPermit.setOrderDesc(result.get("desc").toString());
        unionMainPermit.setSysOrderNo(orderNo);
        unionMainPermit.setOrderStatus(MainConstant.PERMIT_ORDER_STATUS_UNPAY);
        unionMainPermit.setValidity(validity);
        this.insert(unionMainPermit);

        UnionMember member = unionMemberService.getOwnerByBusId(busId);
        if(member != null){
            UnionMain main = unionMainService.getById(member.getUnionId());
            if(main != null){
                //将联盟有效期续期
                UnionMain unionMain = new UnionMain();
                unionMain.setId(main.getId());
                unionMain.setUnionValidity(validity);
                unionMainService.updateById(unionMain);
            }
        }
        redisCacheUtil.remove(paramKey);
        redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_003, 60l);//支付成功
    }

}
