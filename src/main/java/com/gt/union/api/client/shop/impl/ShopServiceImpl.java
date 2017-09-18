package com.gt.union.api.client.shop.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.RequestUtils;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.util.entity.result.shop.WsWxShopInfoExtend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@Service
public class ShopServiceImpl implements ShopService {

	@Value("${wxmp.url}")
	private String wxmpUrl;


	@Override
	public List<WsWxShopInfoExtend> listByBusId(Integer busId) {
		String url = wxmpUrl + "/8A5DA52E/shopapi/6F6D9AD2/79B4DE7C/queryWxShopByBusId.do";
		try {
			RequestUtils req = new RequestUtils<Integer>();
			req.setReqdata(busId);
			Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(req),url, Map.class, ConfigConstant.WXMP_SIGN_KEY);
			if(CommonUtil.isEmpty(result)){
				return null;
			}
			Object data = result.get("data");
			if(CommonUtil.isEmpty(data)){
				return null;
			}
			List<WsWxShopInfoExtend> list = JSONArray.parseArray(data.toString(), WsWxShopInfoExtend.class);
			return list;
		}catch (Exception e){
			return null;
		}
	}
}
