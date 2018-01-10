package com.gt.union.api.client.pay.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.KeysUtil;
import com.gt.api.util.RequestUtils;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.pay.entity.PayParam;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.util.entity.param.pay.ApiEnterprisePayment;
import com.gt.util.entity.param.pay.SubQrPayParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    public String qrCodePay(PayParam payParam){
        StringBuilder builder = new StringBuilder("?");
        builder.append("totalFee=").append(payParam.getTotalFee())
                .append("&model=").append(ConfigConstant.PAY_MODEL)
                .append("&busId=").append(CommonUtil.isEmpty(payParam.getBusId()) ? PropertiesUtil.getDuofenBusId() : payParam.getBusId())
                .append("&appidType=").append(CommonUtil.isEmpty(payParam.getAppidType()) ? 0 : payParam.getAppidType())
                .append("&appid=").append(payParam.getPayDuoFen() ? PropertiesUtil.getDuofenAppid() : CommonUtil.isEmpty(payParam.getAppid()) ? "" : payParam.getAppid())
                .append("&orderNum=").append(payParam.getOrderNum())
                .append("&desc=").append(CommonUtil.isEmpty(payParam.getDesc()) ? "" : payParam.getDesc())
                .append("&isreturn=").append(payParam.getIsreturn())
                .append("&returnUrl=").append(CommonUtil.isEmpty(payParam.getReturnUrl()) ? "" : payParam.getReturnUrl())
                .append("&notifyUrl=").append(CommonUtil.isEmpty(payParam.getNotifyUrl()) ? "" : payParam.getNotifyUrl())
                .append("&isSendMessage=").append(payParam.getIsSendMessage())
                .append("&sendUrl=").append(CommonUtil.isEmpty(payParam.getSendUrl()) ? "" : payParam.getSendUrl())
                .append("&payWay=").append(payParam.getPayWay())
                .append("&sourceType=").append(1);
        if(CommonUtil.isNotEmpty(payParam.getExtend())){
            builder.append("&extend=").append(JSON.toJSONString(payParam.getExtend()));
        }
        String param = builder.toString();
        logger.info("二维码支付请求参数：{}",param);
        return PropertiesUtil.getWxmpUrl() + "/pay/B02A45A5/79B4DE7C/createPayQR.do" + param;
    }

    @Override
    public String pay(PayParam payParam){
        SubQrPayParams subQrPayParams = new SubQrPayParams();
        subQrPayParams.setAppid(payParam.getPayDuoFen() ? PropertiesUtil.getDuofenAppid() : payParam.getAppid());
        subQrPayParams.setAppidType(CommonUtil.isEmpty(payParam.getAppidType()) ? 0 : payParam.getAppidType());
        subQrPayParams.setBusId(CommonUtil.isEmpty(payParam.getBusId()) ? PropertiesUtil.getDuofenBusId() : payParam.getBusId());
        subQrPayParams.setDesc(CommonUtil.isEmpty(payParam.getDesc()) ? "" : payParam.getDesc());
        subQrPayParams.setIsreturn(payParam.getIsreturn());
        subQrPayParams.setReturnUrl(CommonUtil.isEmpty(payParam.getReturnUrl()) ? "" : payParam.getReturnUrl());
        subQrPayParams.setIsSendMessage(payParam.getIsSendMessage());
        subQrPayParams.setSendUrl(CommonUtil.isEmpty(payParam.getSendUrl()) ? "" : payParam.getSendUrl());
        subQrPayParams.setModel(ConfigConstant.PAY_MODEL);
        subQrPayParams.setSourceType(1);
        subQrPayParams.setOrderNum(payParam.getOrderNum());
        subQrPayParams.setTotalFee(payParam.getTotalFee());
        subQrPayParams.setPayWay(payParam.getPayWay());
        subQrPayParams.setNotifyUrl(CommonUtil.isEmpty(payParam.getNotifyUrl()) ? "" : payParam.getNotifyUrl());
        subQrPayParams.setExtend(payParam.getExtend());
        subQrPayParams.setMemberId(payParam.getMemberId());
        logger.info("手机端支付请求参数：{}", JSON.toJSONString(subQrPayParams));
        String obj = "";
        try {
            obj = KeysUtil.getEncString(JSON.toJSONString(subQrPayParams));
        }catch (Exception e){
            logger.error("手机端支付错误：=======>",e);
        }
        return PropertiesUtil.getWxmpUrl() + "/8A5DA52E/payApi/6F6D9AD2/79B4DE7C/payapi.do?obj=" + obj;
    }

	@Override
	public String wxAppPay(PayParam payParam) {
        SubQrPayParams subQrPayParams = new SubQrPayParams();
        subQrPayParams.setAppid(payParam.getPayDuoFen() ? PropertiesUtil.getDuofenAppid() : payParam.getAppid());
        subQrPayParams.setAppidType(1);
        subQrPayParams.setBusId(CommonUtil.isEmpty(payParam.getBusId()) ? PropertiesUtil.getDuofenBusId() : payParam.getBusId());
        subQrPayParams.setDesc(CommonUtil.isEmpty(payParam.getDesc()) ? "" : payParam.getDesc());
        subQrPayParams.setIsreturn(payParam.getIsreturn());
        subQrPayParams.setReturnUrl(CommonUtil.isEmpty(payParam.getReturnUrl()) ? "" : payParam.getReturnUrl());
        subQrPayParams.setIsSendMessage(payParam.getIsSendMessage());
        subQrPayParams.setSendUrl(CommonUtil.isEmpty(payParam.getSendUrl()) ? "" : payParam.getSendUrl());
        subQrPayParams.setModel(ConfigConstant.PAY_MODEL);
        subQrPayParams.setSourceType(1);
        subQrPayParams.setOrderNum(payParam.getOrderNum());
        subQrPayParams.setTotalFee(payParam.getTotalFee());
        subQrPayParams.setPayWay(payParam.getPayWay());
        subQrPayParams.setNotifyUrl(CommonUtil.isEmpty(payParam.getNotifyUrl()) ? "" : payParam.getNotifyUrl());
        subQrPayParams.setExtend(payParam.getExtend());
        logger.info("微信小程序支付请求参数：{}", JSON.toJSONString(subQrPayParams));
        String obj = "";
        try {
            obj = KeysUtil.getEncString(JSON.toJSONString(subQrPayParams));
        }catch (Exception e){
            logger.error("微信小程序支付错误：=======>",e);
        }
        return PropertiesUtil.getWxmpUrl() + "/wxPay/79B4DE7C/commonpayVerApplet2_0.do?obj=" + obj;
	}

	@Override
    public GtJsonResult enterprisePayment(String partnerTradeNo, String openid, String desc, Double amount, Integer paySource){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("amount", amount);
        data.put("desc", desc);
        data.put("busId", PropertiesUtil.getDuofenBusId());
        data.put("appid", PropertiesUtil.getDuofenAppid());
        data.put("model", ConfigConstant.ENTERPRISE_PAY_MODEL);
        data.put("partner_trade_no", partnerTradeNo);
        data.put("openid", openid);
        data.put("paySource", paySource);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("reqdata", data);
        logger.info("商家提现请求参数：{}",JSONObject.toJSONString(param));
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/payApi/6F6D9AD2/79B4DE7C/enterprisePayment.do";
        Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param),url, Map.class, PropertiesUtil.getWxmpSignKey());
        logger.info("商家提现，结果：{}", result.toString());
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
