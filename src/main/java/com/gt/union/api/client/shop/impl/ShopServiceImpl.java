package com.gt.union.api.client.shop.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.RequestUtils;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.util.entity.result.shop.WsWxShopInfoExtend;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@Service
public class ShopServiceImpl implements ShopService {


	@Override
	public List<Map<String, Object>> listByBusId(Integer busId) {
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/shopapi/6F6D9AD2/79B4DE7C/queryWxShopByBusId.do";
		try {
			RequestUtils req = new RequestUtils<Integer>();
			req.setReqdata(busId);
			Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(req),url, Map.class, PropertiesUtil.getWxmpSignKey());
			if(CommonUtil.isEmpty(result)){
				return null;
			}
			Object data = result.get("data");
			if(CommonUtil.isEmpty(data)){
				return null;
			}
			List<WsWxShopInfoExtend> list = JSONArray.parseArray(data.toString(), WsWxShopInfoExtend.class);
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			for(WsWxShopInfoExtend info : list){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("name",info.getBusinessName());
				map.put("id",info.getId());
				dataList.add(map);
			}
			return dataList;
		}catch (Exception e){
			return null;
		}
	}

	@Override
	public List<WsWxShopInfoExtend> listByIds(List<Integer> list) {
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/shopapi/6F6D9AD2/79B4DE7C/findByIds.do";
		try {
			RequestUtils req = new RequestUtils<Integer>();
			req.setReqdata(list);
			Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(req),url, Map.class, PropertiesUtil.getWxmpSignKey());
			if(CommonUtil.isEmpty(result)){
				return null;
			}
			Object data = result.get("data");
			if(CommonUtil.isEmpty(data)){
				return null;
			}
			List<WsWxShopInfoExtend> shops = JSONArray.parseArray(data.toString(), WsWxShopInfoExtend.class);
			return shops;
		}catch (Exception e){
			return null;
		}
	}
}
