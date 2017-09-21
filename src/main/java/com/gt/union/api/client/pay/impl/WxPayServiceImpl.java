package com.gt.union.api.client.pay.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.KeysUtil;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.brokerage.service.IUnionBrokerageWithdrawalService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.opportunity.constant.OpportunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付服务
 * Created by Administrator on 2017/8/22 0022.
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

    @Autowired
    private IUnionBrokerageWithdrawalService unionBrokerageWithdrawalService;

    @Autowired
    private IBusUserService busUserService;

    @Value("${wx.duofen.busId}")
    private Integer duofenBusId;

    @Value("${wxmp.url}")
    private String wxmpUrl;

    @Override
    public int pay(Map<String,Object> param) {
        String url = wxmpUrl + "/8A5DA52E/payApi/6F6D9AD2/79B4DE7C/payapi.do";
        try {
            Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param),url, Map.class, ConfigConstant.WXMP_SIGN_KEY);
            if(CommonUtil.isEmpty(result)){
                return 0;
            }
            if(CommonUtil.toInteger(result.get("code")) != 0){
                return 0;
            }
        }catch (Exception e){
            return 0;
        }
        return 1;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public int enterprisePayment(Integer busId, Integer memberId, String openid, Double fee, Integer verifierId) throws Exception {
        double sumInCome = unionBrokerageIncomeService.getSumInComeUnionBrokerage(busId);
        double sumWithdrawal = unionBrokerageWithdrawalService.getSumWithdrawalsUnionBrokerage(busId);
        if(sumInCome <= 0){
            throw new BusinessException("可提佣金不足");
        }
        if(sumInCome < fee){
            throw new BusinessException("可提佣金不足");
        }
        if(BigDecimalUtil.subtract(sumInCome,sumWithdrawal).doubleValue() < fee){
            throw new BusinessException("可提佣金不足");
        }
        String url = wxmpUrl + "/8A5DA52E/payApi/6F6D9AD2/79B4DE7C/enterprisePayment.do";
        WxPublicUsers wxPublicUsers = busUserService.getWxPublicUserByBusId(duofenBusId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appid", wxPublicUsers.getAppid());
        params.put("partner_trade_no", OpportunityConstant.ORDER_PREFIX + System.currentTimeMillis());
        params.put("openid", openid);
        params.put("desc", "联盟佣金提现");
        params.put("model", ConfigConstant.ENTERPRISE_PAY_MODEL);
        params.put("busId", busId);
        params.put("mchid", wxPublicUsers.getMchId());
        params.put("withdrawalsBusId", busId);
        params.put("memberId", memberId);
        params.put("amount", fee);
        try {
            Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(params),url, Map.class, ConfigConstant.WXMP_SIGN_KEY);
            if(CommonUtil.isEmpty(result)){
                return 0;
            }
            if(CommonUtil.toInteger(result.get("code")) != 0){
                return 0;
            }

            //添加提现记录
            UnionBrokerageWithdrawal withdrawal = new UnionBrokerageWithdrawal();
            withdrawal.setBusId(busId);
            withdrawal.setCreatetime(new Date());
            withdrawal.setDelStatus(CommonConstant.DEL_STATUS_NO);
            withdrawal.setMoney(fee);
            withdrawal.setThirdMemberId(memberId);
            withdrawal.setVerifierId(verifierId);
            unionBrokerageWithdrawalService.insert(withdrawal);
        }catch (Exception e){
            return 0;
        }
        return 1;
    }

	@Override
	public String wxPay(Map<String, Object> data) {
        String obj = KeysUtil.encodeBytes(JSON.toJSONBytes(data));
        return wxmpUrl + "8A5DA52E/payApi/6F6D9AD2/79B4DE7C/payapi.do?obj="+obj;
	}


}
