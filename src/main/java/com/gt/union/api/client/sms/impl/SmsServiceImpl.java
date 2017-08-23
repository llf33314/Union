package com.gt.union.api.client.sms.impl;

import com.alibaba.fastjson.JSON;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/22 0022.
 */
@Service
public class SmsServiceImpl implements SmsService {


	@Override
	public int sendSms(Map<String,Object> param) {
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/smsapi/6F6D9AD2/79B4DE7C/sendSmsOld.do";
		try {
			String result = SignHttpUtils.postByHttp(url, param, CommonConstant.WXMP_SIGN_KEY);
			if(StringUtil.isEmpty(result)){
				return 0;
			}
			Map<String,Object> data= JSON.parseObject(result,Map.class);
			if(CommonUtil.toInteger(data.get("code")) != 0){
				return 0;
			}
		}catch (Exception e){
			return 0;
		}
		return 1;
	}
}
