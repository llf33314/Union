package com.gt.union.api.client.erp.impl;

import com.alibaba.fastjson.JSON;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.erp.ErpService;
import com.gt.union.common.util.ApiResultHandlerUtil;
import com.gt.union.common.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hongjiye
 * @time 2017-12-04 15:27
 **/
@Service
public class ErpServiceImpl implements ErpService {

	private Logger logger = LoggerFactory.getLogger(ErpServiceImpl.class);

	@Override
	public List<Map> listErpByBusId(Integer busId) {
		logger.info("根据商家id获取erp列表，busId:{}", busId);
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/ErploginApi/getErpListApi.do";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("userId",busId);
		param.put("loginStyle",1);
		List<Map> list = null;
		try{
			String result = SignHttpUtils.WxmppostByHttp(url, param, PropertiesUtil.getWxmpSignKey());
			Map data = ApiResultHandlerUtil.getDataObject(result,Map.class);
			list = JSON.parseArray(data.get("menusLevelList").toString(),Map.class);
		}catch (Exception e){
			return null;
		}
		return list;
	}
}
