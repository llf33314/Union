package com.gt.union.api.client.staff.impl;

import com.alibaba.fastjson.JSONArray;
import com.gt.api.bean.session.TCommonStaff;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.staff.ITCommonStaffService;
import com.gt.union.common.util.ApiResultHandlerUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.finance.verifier.entity.UnionVerifier;
import com.gt.union.finance.verifier.service.IUnionVerifierService;
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
 * @time 2017-12-13 10:26
 **/
@Service
public class TCommonStaffServiceImpl implements ITCommonStaffService {

	private Logger logger = LoggerFactory.getLogger(TCommonStaffServiceImpl.class);

	@Autowired
	private IUnionVerifierService unionVerifierService;

	@Override
	public List<TCommonStaff> listTCommonStaffByShopId(Integer shopId, Integer busId) {
		logger.info("根据门店id获取员工列表shopId：{}", shopId);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("shopId", shopId);
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/staffApiMsg/getStaffListShopId.do";
		try{
			String result = SignHttpUtils.WxmppostByHttp(url, param, PropertiesUtil.getWxmpSignKey());
			logger.info("根据门店id获取员工列表，结果：{}", result);
			Map map = ApiResultHandlerUtil.getDataObject(result, Map.class);
			List<TCommonStaff> list = JSONArray.parseArray(map.get("staffList").toString(), TCommonStaff.class);
			List exitsList = new ArrayList<>();
			if(ListUtil.isNotEmpty(list)){
				List<UnionVerifier> verifiers = unionVerifierService.listFinanceByBusId(busId);
				if(ListUtil.isNotEmpty(verifiers)){
					for(TCommonStaff staff : list){
						for(UnionVerifier verifier : verifiers){
							if(staff.getId().equals(verifier.getEmployeeId())){
								exitsList.add(staff);
								break;
							}
						}
					}
					list.removeAll(exitsList);
				}else {
					return list;
				}
			}
			return list;
		}catch (Exception e){
			logger.error("根据门店id获取员工列表错误", e);
			return null;
		}
	}

	@Override
	public TCommonStaff getTCommonStaffById(Integer staffId) {
		logger.info("根据员工id获取员工信息staffId：{}", staffId);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("staffId", staffId);
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/staffApiMsg/getStaffId.do";
		try{
			String result = SignHttpUtils.WxmppostByHttp(url, param, PropertiesUtil.getWxmpSignKey());
			logger.info("根据员工id获取员工信息，结果：{}", result);
			return ApiResultHandlerUtil.getDataObject(result, TCommonStaff.class);
		}catch (Exception e){
			logger.error("根据员工id获取员工信息错误", e);
			return null;
		}
	}
}
