package com.gt.union.api.client.address.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.union.api.client.address.AddressService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/22 0022.
 */
@Service
public class AddressServiceImpl implements AddressService {

	@Override
	public List<Map> getByIds(Map<String,Object> param) {
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/shopapi/6F6D9AD2/79B4DE7C/queryBasisCityIds.do";
		try {
			Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param),url, Map.class, CommonConstant.WXMP_SIGN_KEY);
			if(CommonUtil.isEmpty(result)){
				return null;
			}
			Object data = result.get("data");
			if(CommonUtil.isEmpty(data)){
				return null;
			}
			List<Map> list = JSONArray.parseArray(data.toString(), Map.class);
			return list;
		}catch (Exception e){
			return null;
		}
	}

	@Override
	public List<Map> getByCityCode(Map<String, Object> param) {
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/shopapi/6F6D9AD2/79B4DE7C/queryBasisCityIds.do";
		try {
			Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param),url, Map.class, CommonConstant.WXMP_SIGN_KEY);
			if(CommonUtil.isEmpty(result)){
				return null;
			}
			Object data = result.get("data");
			if(CommonUtil.isEmpty(data)){
				return null;
			}
			List<Map> list = JSONArray.parseArray(data.toString(), Map.class);
			return list;
		}catch (Exception e){
			return null;
		}
	}


}
