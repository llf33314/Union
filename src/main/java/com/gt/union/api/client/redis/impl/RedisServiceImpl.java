package com.gt.union.api.client.redis.impl;

import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.redis.RedisService;
import com.gt.union.common.constant.ConfigConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/22 0022.
 */
@Service
public class RedisServiceImpl implements RedisService {

	@Value("${wxmp.url}")
	private String wxmpUrl;

	@Override
	public void setValue(String redisKey, String redisValue, Integer second) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("redisKey",redisKey);
		param.put("redisValue",redisValue);
		param.put("setime",second);
		String url = wxmpUrl + "/8A5DA52E/redis/SetExApi.do";
		try{
			String result = SignHttpUtils.WxmppostByHttp(url, param, ConfigConstant.WXMP_SIGN_KEY);
		}catch (Exception e){

		}
	}

	@Override
	public String getValue(String redisKey) {
		return null;
	}
}
