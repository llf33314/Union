package com.gt.union.api.client.address.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.union.api.client.address.AddressService;
import com.gt.union.common.util.ApiResultHandlerUtil;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hongjiye
 * Created by Administrator on 2017/1/25 0022.
 */
@Service
public class AddressServiceImpl implements AddressService {

	private Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

	@Override
	public List<Map> getByIds(String ids) {
		logger.info("根据ids获取地址列表，请求参数：{}",JSONObject.toJSONString(ids));
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/shopapi/6F6D9AD2/79B4DE7C/queryBasisCityIds.do";
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("reqdata", ids);
			String result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param),url, String.class, PropertiesUtil.getWxmpSignKey());
			return ApiResultHandlerUtil.listDataObject(result, Map.class);
		}catch (Exception e){
			return null;
		}
	}

	@Override
	public List<Map> getByCityCode(String city_codes) {
		logger.info("根据city_code列表获取地址列表，请求参数:{}", JSONObject.toJSONString(city_codes));
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/shopapi/6F6D9AD2/79B4DE7C/queryBasisByCodes.do";
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("reqdata", city_codes);
			String result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param),url, String.class, PropertiesUtil.getWxmpSignKey());
			return ApiResultHandlerUtil.listDataObject(result, Map.class);
		}catch (Exception e){
			return null;
		}
	}


}
