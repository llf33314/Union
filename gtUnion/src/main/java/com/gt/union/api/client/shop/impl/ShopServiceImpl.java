package com.gt.union.api.client.shop.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.RequestUtils;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.client.shop.vo.ShopVO;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.util.ApiResultHandlerUtil;
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
	public List<ShopVO> listByBusId(Integer busId) {
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/shopapi/6F6D9AD2/79B4DE7C/queryWxShopByBusId.do";
		try {
			RequestUtils req = new RequestUtils<Integer>();
			req.setReqdata(busId);
			String result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(req),url, String.class, PropertiesUtil.getWxmpSignKey());
			List<WsWxShopInfoExtend> list = ApiResultHandlerUtil.listDataObject(result,WsWxShopInfoExtend.class);
			List<ShopVO> dataList = new ArrayList<ShopVO>();
			for(WsWxShopInfoExtend info : list){
				ShopVO vo = new ShopVO();
				vo.setName(info.getBusinessName());
				vo.setId(info.getId());
				dataList.add(vo);
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
			String result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(req),url, String.class, PropertiesUtil.getWxmpSignKey());
			List<WsWxShopInfoExtend> shops = ApiResultHandlerUtil.listDataObject(result,WsWxShopInfoExtend.class);
			return shops;
		}catch (Exception e){
			return null;
		}
	}
}
