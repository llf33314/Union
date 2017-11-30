package com.gt.union.api.client.sms.impl;

import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.RequestUtils;
import com.gt.union.api.amqp.entity.PhoneMessage;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 短信服务api
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
@Service
public class SmsServiceImpl implements SmsService {

	@Override
	public boolean sendSms(PhoneMessage phoneMessage) {
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
			return false;
		}
		return true;
	}
}
