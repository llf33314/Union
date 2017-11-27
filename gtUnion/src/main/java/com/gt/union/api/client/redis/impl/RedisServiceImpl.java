package com.gt.union.api.client.redis.impl;

import com.alibaba.fastjson.JSON;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.redis.RedisService;
import com.gt.union.common.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
@Service
public class RedisServiceImpl implements RedisService {

	private Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);

	@Override
	public int setValue(String redisKey, String redisValue, Integer second) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("redisKey",redisKey);
		param.put("redisValue",redisValue);
		param.put("setime",second);
		logger.info("设置公共redis值，参数：{}", JSON.toJSONString(param));
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/redis/SetExApi.do";
		try{
			SignHttpUtils.WxmppostByHttp(url, param, PropertiesUtil.getWxmpSignKey());
		}catch (Exception e){
			return 0;
		}
		return 1;
	}

	@Override
	public String getValue(String redisKey) {
		return null;
	}
}
