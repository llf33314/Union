package com.gt.union.api.erp.jxc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.gt.union.api.erp.jxc.service.JxcAuthorityService;
import com.gt.union.api.erp.jxc.util.HttpClientUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import com.gt.union.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hongjiye
 * @time 2017-12-05 16:27
 **/
@Service
public class JxcAuthorityServiceImpl implements JxcAuthorityService{

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Override
	public String getJxcAuthority() {
		String url = PropertiesUtil.getJxcUrl() + "/erp/b/login";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", PropertiesUtil.getJxcAccount());
		params.put("pwd", PropertiesUtil.getJxcPwd());
		try {
			JSONObject jsonObject = JSONObject.parseObject(HttpClientUtil.httpPostRequest(url, params));
			JSONObject tokens = jsonObject.getJSONObject("data");
			String token = tokens.getString("token");
			if(StringUtil.isNotEmpty(token)){
				String key = RedisKeyUtil.getJxcAuthorityKey();
				redisCacheUtil.set(key, token, 10800L);
			}
			return token;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
