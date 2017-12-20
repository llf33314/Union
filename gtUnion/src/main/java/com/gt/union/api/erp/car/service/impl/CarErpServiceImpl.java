package com.gt.union.api.erp.car.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.erp.vo.ErpServerVO;
import com.gt.union.api.erp.car.service.CarErpService;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hongjiye
 * @time 2017-12-19 10:00
 **/
@Service
public class CarErpServiceImpl implements CarErpService {

	private Logger logger = LoggerFactory.getLogger(CarErpServiceImpl.class);

	@Override
	public List<ErpServerVO> listErpServer(Integer shopId, String search, Integer busId, Page page) {
		logger.info("查询车小算服务项目列表门店id：{}，商家id：{}，search：{}", shopId, busId, search);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("shopId",shopId);
		param.put("pageSize",page.getSize());
		param.put("pageNum",page.getCurrent());
		param.put("keyValue",search);
		try{
			String result = SignHttpUtils.WxmppostByHttp(PropertiesUtil.getCarUrl() + "/openApi/v1/serveItemPage", param,PropertiesUtil.getWxmpSignKey());
			logger.info("查询车小算服务项目列表，结果：{}", result);
			JSONObject jsonObject = JSONObject.parseObject(result);
			if(jsonObject.get("status").equals(1000)){
				JSONObject object = jsonObject.getJSONObject("data");
				List<Map> list = JSONArray.parseArray(object.get("list").toString(),Map.class);
				List<ErpServerVO> data = new ArrayList(list.size());
				for(Map map : list){
					ErpServerVO vo = new ErpServerVO();
					vo.setServerId(CommonUtil.toInteger(map.get("id")));
					vo.setServerName(CommonUtil.toString(map.get("serveItemName")));
					data.add(vo);
				}
				page.setTotal(CommonUtil.toInteger(object.get("total")));
				return data;
			}
		}catch (Exception e){
			logger.error("查询车小算服务项目列表错误", e);
		}
		return null;
	}
}
