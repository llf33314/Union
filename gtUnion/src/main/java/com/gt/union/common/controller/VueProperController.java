package com.gt.union.common.controller;

import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.PropertiesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hongjiye
 * @time 2017-12-21 17:18
 **/
@Api(description = "前端vue配置")
@RestController
@RequestMapping("/vue/prop")
public class VueProperController {

	@ApiOperation(value = "根据门店列表获取员工列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/79B4DE7C/urls", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GtJsonResult listStaffByShopId(HttpServletRequest request) throws Exception {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("socketUrl", PropertiesUtil.getSocketUrl());
		data.put("wxmpUrl", PropertiesUtil.getWxmpUrl());
		data.put("unionUrl", PropertiesUtil.getUnionUrl());
		data.put("sucUrl",PropertiesUtil.getSucUrl());
		return GtJsonResult.instanceSuccessMsg(data);
	}
}
