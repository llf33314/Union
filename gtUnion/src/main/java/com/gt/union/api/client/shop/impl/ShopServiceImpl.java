package com.gt.union.api.client.shop.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.RequestUtils;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.client.shop.vo.ShopVO;
import com.gt.union.common.util.ApiResultHandlerUtil;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.util.entity.result.shop.WsWxShopInfoExtend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private Logger logger = LoggerFactory.getLogger(ShopServiceImpl.class);


	@Override
	public List<ShopVO> listByBusId(Integer busId) {
		logger.info("根据商家id获取门店列表信息，busId:{}", busId);
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/shopapi/6F6D9AD2/79B4DE7C/queryWxShopByBusId.do";
		try {
			RequestUtils req = new RequestUtils<Integer>();
			req.setReqdata(busId);
			String result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(req),url, String.class, PropertiesUtil.getWxmpSignKey());
			logger.info("根据商家id获取门店列表信息，结果：{}", result);
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
			logger.error("根据商家id获取门店列表信息错误", e);
			return null;
		}
	}

	@Override
	public List<WsWxShopInfoExtend> listByIds(List<Integer> list) {
		logger.info("根据门店id列表获取门店列表信息，list:{}", JSON.toJSONString(list));
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/shopapi/6F6D9AD2/79B4DE7C/findByIds.do";
		try {
			RequestUtils req = new RequestUtils<Integer>();
			req.setReqdata(list);
			String result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(req),url, String.class, PropertiesUtil.getWxmpSignKey());
			logger.info("根据门店id列表获取门店列表信息，结果：{}", result);
			List<WsWxShopInfoExtend> shops = ApiResultHandlerUtil.listDataObject(result,WsWxShopInfoExtend.class);
			return shops;
		}catch (Exception e){
			logger.error("根据门店id列表获取门店列表信息错误", e);
			return null;
		}
	}

	@Override
	public WsWxShopInfoExtend getById(Integer id) {
		logger.info("根据门店id获取门店信息，id:{}", JSON.toJSONString(id));
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/shopapi/6F6D9AD2/79B4DE7C/findByIds.do";
		try {
			List<Integer> list = new ArrayList<Integer>();
			list.add(id);
			RequestUtils req = new RequestUtils<Integer>();
			req.setReqdata(list);
			String result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(req),url, String.class, PropertiesUtil.getWxmpSignKey());
			logger.info("根据门店id获取门店信息，结果：{}", result);
			List<WsWxShopInfoExtend> shops = ApiResultHandlerUtil.listDataObject(result,WsWxShopInfoExtend.class);
			return CommonUtil.isNotEmpty(shops) ? shops.get(0) : null;
		}catch (Exception e){
			logger.error("根据门店id列表获取门店列表信息错误", e);
			return null;
		}
	}
}
