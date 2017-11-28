package com.gt.union.api.client.pay.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.KeysUtil;
import com.gt.api.util.RequestUtils;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.util.entity.param.pay.ApiEnterprisePayment;
import com.gt.util.entity.param.pay.SubQrPayParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 微信支付服务
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
@Service
public class WxPayServiceImpl implements WxPayService {
    private Logger logger = LoggerFactory.getLogger(WxPayServiceImpl.class);

    @Override
    public String qrCodePay(Double totalFee, Integer appidType, String orderNum, String desc, Integer isreturn, String returnUrl, String notifyUrl, Integer isSendMessage,
                            String sendUrl, Integer payWay, Map extend){
        StringBuilder builder = new StringBuilder("?");
        builder.append("totalFee=").append(totalFee)
                .append("&model=").append(ConfigConstant.PAY_MODEL)
                .append("&busId=").append(PropertiesUtil.getDuofenBusId())
                .append("&appidType=").append(appidType)
                .append("&appid=").append(PropertiesUtil.getDuofenAppid())
                .append("&orderNum=").append(orderNum)
                .append("&desc=").append(desc)
                .append("&isreturn=").append(isreturn)
                .append("&returnUrl=").append(returnUrl)
                .append("&notifyUrl=").append(notifyUrl)
                .append("&isSendMessage=").append(isSendMessage)
                .append("&sendUrl=").append(sendUrl)
                .append("&payWay=").append(payWay)
                .append("&sourceType=").append(1);
        if(CommonUtil.isNotEmpty(extend)){
            builder.append("&extend=").append(JSON.toJSONString(extend));
        }
        String param = builder.toString();
        logger.info("二维码支付请求参数：{}",param);
        return  PropertiesUtil.getWxmpUrl() + "/pay/B02A45A5/79B4DE7C/createPayQR.do" + param;
    }

    @Override
    public String pay(Double totalFee, Integer appidType, String orderNum, String desc, Integer isreturn, String returnUrl, String notifyUrl, Integer isSendMessage,
                      String sendUrl, Integer payWay, Map extend){
        SubQrPayParams payParams = new SubQrPayParams();
        payParams.setAppid(PropertiesUtil.getDuofenAppid());
        payParams.setAppidType(appidType);
        payParams.setBusId(PropertiesUtil.getDuofenBusId());
        payParams.setDesc(desc);
        payParams.setIsreturn(isreturn);
        payParams.setReturnUrl(returnUrl);
        payParams.setIsSendMessage(isSendMessage);
        payParams.setSendUrl(sendUrl);
        payParams.setModel(ConfigConstant.PAY_MODEL);
        payParams.setSourceType(1);
        payParams.setOrderNum(orderNum);
        payParams.setTotalFee(totalFee);
        payParams.setPayWay(payWay);
        payParams.setNotifyUrl(notifyUrl);
        payParams.setExtend(extend);
        logger.info("手机端支付请求参数：{}", JSON.toJSONString(payParams));
        String obj = null;
        try {
            obj = KeysUtil.getEncString(JSON.toJSONString(payParams));
        }catch (Exception e){
            logger.error("手机端支付错误：=======>",e);
        }
        return PropertiesUtil.getWxmpUrl() + "/8A5DA52E/payApi/6F6D9AD2/79B4DE7C/payapi.do?obj=" + obj;
    }

    @Override
    public GtJsonResult enterprisePayment(String partnerTradeNo, String openid, String desc, Double amount, Integer paySource){
        ApiEnterprisePayment payment = new ApiEnterprisePayment();
        payment.setAmount(amount);
        payment.setDesc(desc);
        payment.setBusId(PropertiesUtil.getDuofenBusId());
        payment.setAppid(PropertiesUtil.getDuofenAppid());
        payment.setModel(ConfigConstant.ENTERPRISE_PAY_MODEL);
        payment.setPartner_trade_no(partnerTradeNo);
        payment.setOpenid(openid);
        payment.setPaySource(paySource);
        RequestUtils requestUtils = new RequestUtils();
        requestUtils.setReqdata(payment);
        logger.info("商家提现请求参数：{}",JSONObject.toJSONString(requestUtils));
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/payApi/6F6D9AD2/79B4DE7C/enterprisePayment.do";
        Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(requestUtils),url, Map.class, PropertiesUtil.getWxmpSignKey());
        if(CommonUtil.isEmpty(result)){
            return GtJsonResult.instanceErrorMsg("提现失败");
        }
        if(CommonUtil.toInteger(result.get("code")) == 0){
            return GtJsonResult.instanceSuccessMsg();
        }else {
            return GtJsonResult.instanceErrorMsg(CommonUtil.isNotEmpty(result.get("msg")) ? result.get("msg").toString() : "提现失败");
        }
    }

}
