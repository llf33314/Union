package com.gt.union.api.erp.jxc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.gt.union.api.client.pay.impl.WxPayServiceImpl;
import com.gt.union.api.erp.jxc.service.JxcAuthorityService;
import com.gt.union.api.erp.jxc.service.JxcProductClassService;
import com.gt.union.api.erp.jxc.util.HttpClientUtil;
import com.gt.union.common.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 进销存商品分类api
 * @author hongjiye
 * @time 2017-12-06 16:41
 **/
@Service
public class JxcProductClassServiceImpl implements JxcProductClassService {

	private Logger logger = LoggerFactory.getLogger(JxcProductClassServiceImpl.class);

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private JxcAuthorityService jxcAuthorityService;

	@Override
	public String listProductClassByBusId(Integer busId) {
		logger.info("根据商家id查询商品分类列表：{}",busId);
		String url = PropertiesUtil.getJxcUrl() + "/erp/order/news/basic/product/type/" + busId;
		String key = RedisKeyUtil.getJxcAuthorityKey();
		String token = redisCacheUtil.get(key);
		if(CommonUtil.isEmpty(token)){
			token = jxcAuthorityService.getJxcAuthority();
		}
		try {
			String result = HttpClientUtil.httpGetRequest(url, null, token);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
