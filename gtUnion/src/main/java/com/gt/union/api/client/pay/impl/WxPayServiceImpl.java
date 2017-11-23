package com.gt.union.api.client.pay.impl;

import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 微信支付服务
 * Created by Administrator on 2017/8/22 0022.
 */
@Service
public class WxPayServiceImpl implements WxPayService {

//    @Autowired
//    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

//    @Autowired
//    private IUnionBrokerageWithdrawalService unionBrokerageWithdrawalService;

    @Autowired
    private IBusUserService busUserService;


    @Override
    public int pay(Map<String, Object> param) {
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/payApi/6F6D9AD2/79B4DE7C/payapi.do";
        try {
            Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param), url, Map.class, PropertiesUtil.getWxmpSignKey());
            if (CommonUtil.isEmpty(result)) {
                return 0;
            }
            if (CommonUtil.toInteger(result.get("code")) != 0) {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public int enterprisePayment(Integer busId, Integer memberId, String openid, Double fee, Integer verifierId) throws Exception {
//        double sumInCome = unionBrokerageIncomeService.getSumInComeUnionBrokerage(busId);
//        double sumWithdrawal = unionBrokerageWithdrawalService.getSumWithdrawalsUnionBrokerage(busId);
//        if(sumInCome <= 0){
//            throw new BusinessException("可提佣金不足");
//        }
//        if(sumInCome < fee){
//            throw new BusinessException("可提佣金不足");
//        }
//        if(BigDecimalUtil.subtract(sumInCome,sumWithdrawal).doubleValue() < fee){
//            throw new BusinessException("可提佣金不足");
//        }
//        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/payApi/6F6D9AD2/79B4DE7C/enterprisePayment.do";
//        WxPublicUsers wxPublicUsers = busUserService.getWxPublicUserByBusId(PropertiesUtil.getDuofenBusId());
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("appid", wxPublicUsers.getAppid());
//        params.put("partner_trade_no", OpportunityConstant.ORDER_PREFIX + System.currentTimeMillis());
//        params.put("openid", openid);
//        params.put("desc", "联盟佣金提现");
//        params.put("model", ConfigConstant.ENTERPRISE_PAY_MODEL);
//        params.put("busId", busId);
//        params.put("amount", fee);
//        params.put("paySource",0);
//        Map<String,Object> data = new HashMap<String,Object>();
//        data.put("reqdata",params);
//        try {
//            Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(data),url, Map.class, PropertiesUtil.getWxmpSignKey());
//            if(CommonUtil.isEmpty(result)){
//                return 0;
//            }
//            if(CommonUtil.toInteger(result.get("code")) != 0){
//                return 0;
//            }
//
//            //添加提现记录
//            UnionBrokerageWithdrawal withdrawal = new UnionBrokerageWithdrawal();
//            withdrawal.setBusId(busId);
//            withdrawal.setCreatetime(new Date());
//            withdrawal.setDelStatus(CommonConstant.DEL_STATUS_NO);
//            withdrawal.setMoney(fee);
//            withdrawal.setThirdMemberId(memberId);
//            withdrawal.setVerifierId(verifierId);
//            unionBrokerageWithdrawalService.insert(withdrawal);
//        }catch (Exception e){
//            return 0;
//        }
        return 1;
    }

    @Override
    public String wxPay(Map<String, Object> data) {
        String obj = null;
//        try {
//            KeysUtil keysUtil = new KeysUtil();
//            obj = keysUtil.getEncString(JSON.toJSONString(data));
//        }catch (Exception e){
//
//        }
        return PropertiesUtil.getWxmpUrl() + "/8A5DA52E/payApi/6F6D9AD2/79B4DE7C/payapi.do?obj=" + obj;
    }


}
