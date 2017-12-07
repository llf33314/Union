package com.gt.union.api.erp.jxc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.erp.jxc.entity.JxcProductPO;
import com.gt.union.api.erp.jxc.service.JxcProductService;
import com.gt.union.api.erp.jxc.util.HttpClientUtil;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 进销存商品api
 * @author hongjiye
 * @time 2017-12-07 14:08
 **/
@Service
public class JxcProductServiceImpl implements JxcProductService{

	private Logger logger = LoggerFactory.getLogger(JxcProductClassServiceImpl.class);

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Override
	public Page<List<JxcProductPO>> listProductByShopIdAndClassIdAndSearchPage(Integer shopId, Integer classId, String search, Integer pageIndex, Integer pageCount) {
		String url = PropertiesUtil.getJxcUrl() + "/erp/order/news/all/invenotry";
		String key = RedisKeyUtil.getJxcAuthorityKey();
		try {
			Page page = new Page<>();
			String token = redisCacheUtil.get(key);
			if(CommonUtil.isNotEmpty(token)){
				token = JSON.parseObject(token,String.class);
			}
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("shopId",shopId);
			param.put("classId",classId);
			param.put("search",search);
			param.put("pageIndex",pageIndex);
			param.put("pageCount",pageCount);
			logger.info("根据门店id和商品分类id和搜索条件(名称/条码/编码/全拼码) 分页查询商品：{}", JSON.toJSONString(param));

			String result = HttpClientUtil.httpGetRequest(url, param, token);
			JSONObject jsonObject = JSONObject.parseObject(result);
			String data = jsonObject.getJSONObject("data").toJSONString();
			jsonObject = JSONObject.parseObject(data);
			List<JxcProductPO> list = JSONArray.parseArray(jsonObject.getString("content"), JxcProductPO.class);
			page.setRecords(list);
			page.setCurrent(pageIndex);
			page.setSearchCount(false);
			page.setSize(CommonUtil.toInteger(jsonObject.get("size")));
			page.setTotal(CommonUtil.toInteger(jsonObject.get("totalElements")));
			return page;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
