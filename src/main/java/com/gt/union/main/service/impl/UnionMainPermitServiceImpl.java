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
import com.gt.union.main.entity.UnionMainPermitCharge;
import com.gt.union.main.mapper.UnionMainPermitMapper;
import com.gt.union.main.service.IUnionMainPermitChargeService;
import com.gt.union.main.service.IUnionMainPermitService;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
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
    private IUnionMainPermitChargeService unionMainPermitChargeService;

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
	public UnionMainPermit createPermit(String orderNo, Double pay, Integer busId) {
        UnionMainPermit permit = new UnionMainPermit();
        permit.setBusId(busId);
        permit.setOrderMoney(pay);
        permit.setDelStatus(CommonConstant.DEL_STATUS_NO);
        permit.setCreatetime(new Date());
        permit.setOrderDesc("多粉-创建联盟付款");
        permit.setSysOrderNo(orderNo);
        permit.setOrderStatus(MainConstant.PERMIT_ORDER_STATUS_UNPAY);
        this.insert(permit);
		return permit;
	}

    @Override
    public Map<String, Object> createUnionQRCode(BusUser user, Integer chargeId) throws Exception{
        Map<String, Object> data = new HashMap<String, Object>();
        List<Map> list = dictService.getUnionCreatePackage();
        boolean flag = false;
        for(Map map : list){
            if(map.get("item_key").equals(chargeId)){
                String itemValue = map.get("item_value").toString();
                String[] arrs = itemValue.split(",");
                String isUnionOwnerService = arrs[0];//是否有创建盟主的服务权限 1：是 0：否
                if (isUnionOwnerService.equals("0")) {
                    throw new BusinessException("您没有创建联盟的权限");
                }
                flag = true;
                break;
            }
        }
        if(!flag){
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainPermitCharge charge = unionMainPermitChargeService.getById(chargeId);
        if(charge == null){
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Double pay = charge.getMoney();
        String orderNo = ConfigConstant.CREATE_UNION_PAY_ORDER_CODE + System.currentTimeMillis();
        String only= DateTimeKit.getDateTime(new Date(), DateTimeKit.yyyyMMddHHmmss);
        WxPublicUsers publicUser = busUserService.getWxPublicUserByBusId(PropertiesUtil.getDuofenBusId());
        UnionMainPermit permit = createPermit(orderNo,pay, user.getId());
        data.put("totalFee",pay);
        data.put("busId", PropertiesUtil.getDuofenBusId());
        data.put("sourceType", 1);//是否墨盒支付
        data.put("payWay",0);//系统判断支付方式
        data.put("isreturn",0);//0：不需要同步跳转
        data.put("model", ConfigConstant.PAY_MODEL);
        String encrypt = EncryptUtil.encrypt(PropertiesUtil.getEncryptKey(), orderNo);
        encrypt = URLEncoder.encode(encrypt,"UTF-8");
        data.put("notifyUrl", PropertiesUtil.getUnionUrl() + "/unionMainPermit/79B4DE7C/paymentSuccess/"+encrypt + "/" + only);
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
        redisCacheUtil.set(paramKey, JSON.toJSONString(data), 360l);//5分钟
        redisCacheUtil.set(statusKey,ConfigConstant.USER_ORDER_STATUS_001,300l);//等待扫码状态
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payCreateUnionSuccess(String recordEncrypt, String only) throws Exception{
        //解密订单号
        String orderNo = EncryptUtil.decrypt(PropertiesUtil.getEncryptKey(),recordEncrypt);
        String paramKey = RedisKeyUtil.getCreateUnionPayParamKey(only);
        Object obj = redisCacheUtil.get(paramKey);
        Map<String,Object> result = JSONObject.parseObject(obj.toString(),Map.class);
        String statusKey = RedisKeyUtil.getCreateUnionPayStatusKey(only);
        //添加支付订单
        Integer busId = CommonUtil.toInteger(result.get("payBusId"));
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("sys_order_no",orderNo);
        wrapper.eq("del_status",CommonConstant.DEL_STATUS_NO);
        UnionMainPermit permit = this.selectOne(wrapper);
        Object infoItemKey = result.get("infoItemKey");
        Integer chargeId = CommonUtil.toInteger(infoItemKey);
        UnionMainPermitCharge charge = unionMainPermitChargeService.getById(chargeId);
        int month = (int)(new BigDecimal(charge.getYear()).multiply(new BigDecimal(12)).doubleValue());

        //更新有效期,支付状态
        UnionMainPermit unionMainPermit = new UnionMainPermit();
        unionMainPermit.setId(permit.getId());
        unionMainPermit.setValidity(DateTimeKit.addMonths(month));
        unionMainPermit.setOrderStatus(MainConstant.PERMIT_ORDER_STATUS_SUCCESS);
        this.updateById(unionMainPermit);

        //商家的许可
        UnionMainPermit mainPermit = this.getByBusId(busId);
        //设为没有该许可了
        mainPermit.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(mainPermit);

        UnionMember member = unionMemberService.getOwnerByBusId(busId);
        if(member != null){
            UnionMain main = unionMainService.getById(member.getUnionId());
            if(main != null){
                //将联盟有效期续期
                UnionMain unionMain = new UnionMain();
                unionMain.setId(main.getId());
                unionMain.setUnionValidity(DateTimeKit.addMonths(month));
                unionMainService.updateById(unionMain);
            }
        }
        redisCacheUtil.remove(paramKey);
        redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_003, 60l);//支付成功
    }

}
