package com.gt.union.api.client.erp.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.erp.ErpService;
import com.gt.union.api.client.erp.vo.ErpTypeVO;
import com.gt.union.api.client.erp.vo.ErpServerVO;
import com.gt.union.api.erp.car.service.CarErpService;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.util.ApiResultHandlerUtil;
import com.gt.union.common.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

	@Autowired
	private CarErpService carErpService;

	@Override
	public List<ErpTypeVO> listErpByBusId(Integer busId) {
		logger.info("根据商家id获取erp列表，busId:{}", busId);
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/ErploginApi/getErpListApi.do";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("userId",busId);
		param.put("loginStyle",1);
		param.put("loginUc",0);
		List<ErpTypeVO> dataList = new ArrayList<ErpTypeVO>();
		try{
			String result = SignHttpUtils.WxmppostByHttp(url, param, PropertiesUtil.getWxmpSignKey());
			logger.info("根据商家id获取erp列表，结果：{}", result);
			Map data = ApiResultHandlerUtil.getDataObject(result,Map.class);
			List<ErpTypeVO> list = JSON.parseArray(data.get("menusLevelList").toString(),ErpTypeVO.class);
			for(ErpTypeVO vo : list){
				for(Integer erpType : ConfigConstant.UNION_USER_ERP_TYPE){
					if(erpType.equals(vo.getErpModel())){
						vo.setErpType(vo.getErpModel());
						dataList.add(vo);
					}
				}
			}
		}catch (Exception e){
			logger.error("根据商家id获取erp列表错误",e);
			return null;
		}
		return dataList;
	}

	@Override
	public List<ErpServerVO> listErpServer(Integer shopId, Integer erpModel, String search, Page page, Integer busId) {
		List<ErpServerVO> list = null;
		switch (erpModel){
			case 2:
				//车小算
				list = carErpService.listErpServer(shopId, search, busId, page);
				for(ErpServerVO vo : list){
					vo.setErpType(erpModel);
					vo.setShopId(shopId);
				}
				break;
			default:
				break;
		}
		return list;
	}
}
