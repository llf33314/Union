package com.gt.union.api.erp.jxc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gt.union.api.erp.jxc.entity.JxcProductClass;
import com.gt.union.api.erp.jxc.service.JxcAuthorityService;
import com.gt.union.api.erp.jxc.service.JxcProductClassService;
import com.gt.union.common.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 进销存商品分类api
 * @author hongjiye
 * @time 2017-12-06 16:41
 **/
@Service
public class JxcProductClassServiceImpl implements JxcProductClassService {

	private Logger logger = LoggerFactory.getLogger(JxcProductClassServiceImpl.class);

	@Autowired
	private JxcAuthorityService jxcAuthorityService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Override
	public List<JxcProductClass> listProductClassByBusId(Integer busId) {
		logger.info("根据商家id查询进销存商品分类列表：{}",busId);
		String url = PropertiesUtil.getJxcUrl() + "/erp/order/news/basic/product/type/" + busId;
		String key = RedisKeyUtil.getJxcAuthorityKey();
		try {
			String token = redisCacheUtil.get(key);
			if(CommonUtil.isNotEmpty(token)){
				token = JSON.parseObject(token,String.class);
			}else {
				token = jxcAuthorityService.getJxcAuthority();
			}
			String result = SignRestHttpUtil.reqTokenGetUTF8(url, null, token);
			logger.info("根据商家id查询进销存商品分类列表，结果：{}", result);
			JSONObject jsonObject = JSONObject.parseObject(result);
			List<JxcProductClass> data = JSONArray.parseArray(jsonObject.getJSONArray("data").toJSONString(), JxcProductClass.class);
			return data;
		} catch (Exception e) {
			logger.error("根据商家id查询进销存商品分类列表错误", e);
			e.printStackTrace();
		}
		return null;
	}
}
