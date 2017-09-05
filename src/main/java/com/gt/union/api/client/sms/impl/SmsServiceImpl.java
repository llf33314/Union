package com.gt.union.api.client.sms.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/22 0022.
 */
@Service
public class SmsServiceImpl implements SmsService {

	@Value("${wxmp.url}")
	private String wxmpUrl;

	@Override
	public int sendSms(Map<String,Object> param) {
		String url = wxmpUrl + "/8A5DA52E/smsapi/6F6D9AD2/79B4DE7C/sendSmsOld.do";
		try {
			Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param),url, Map.class, CommonConstant.WXMP_SIGN_KEY);
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
}
