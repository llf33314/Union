package com.gt.union.api.client.sms.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.RequestUtils;
import com.gt.union.api.amqp.entity.PhoneMessage;
import com.gt.union.api.amqp.entity.TemplateSmsMessage;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.RedisCacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 短信服务api
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
@Service
public class SmsServiceImpl implements SmsService {

	private Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Override
	public boolean sendSms(PhoneMessage phoneMessage) {
		logger.info("发送短信：{}", JSON.toJSONString(phoneMessage));
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/smsapi/6F6D9AD2/79B4DE7C/sendSmsOld.do";
		try {
			RequestUtils requestUtils = new RequestUtils<PhoneMessage>();
			requestUtils.setReqdata(phoneMessage);
			Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(requestUtils),url, Map.class, PropertiesUtil.getWxmpSignKey());
			if(CommonUtil.isEmpty(result)){
				return false;
			}
			if(CommonUtil.toInteger(result.get("code")) != 0){
				return false;
			}
		}catch (Exception e){
			logger.error("发送短信错误", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean sendTempSms(TemplateSmsMessage templateSmsMessage) {
		logger.info("发送模板短信：{}", JSON.toJSONString(templateSmsMessage));
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/smsapi/6F6D9AD2/79B4DE7C/sendSmsNew.do";
		try {
			RequestUtils requestUtils = new RequestUtils<TemplateSmsMessage>();
			requestUtils.setReqdata(templateSmsMessage);
			Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(requestUtils),url, Map.class, PropertiesUtil.getWxmpSignKey());
			if(CommonUtil.isEmpty(result)){
				return false;
			}
			if(CommonUtil.toInteger(result.get("code")) != 0){
				return false;
			}
		}catch (Exception e){
			logger.error("发送模板短信错误", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean checkPhoneCode(Integer type, String code, String phone) {
		logger.info("短信验证码校验type：{}，code：{}，phone：{}", type, code, phone);
		try{
			String key = type + ":" + phone;
			if(redisCacheUtil.exists(key)){
				String checkCode = redisCacheUtil.get(type + ":" + phone);
				checkCode = JSONArray.parseObject(checkCode, String.class);
				if(CommonUtil.isEmpty(checkCode)){
					return false;
				}
				if(!checkCode.equals(code)){
					return false;
				}
			}else {
				return false;
			}
		}catch (Exception e){
			logger.error("短信验证码校验错误", e);
			return false;
		}
		return true;
	}
}
