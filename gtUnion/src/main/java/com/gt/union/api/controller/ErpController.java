package com.gt.union.api.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.erp.ErpService;
import com.gt.union.api.client.erp.vo.ErpModelVO;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.client.shop.vo.ShopVO;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GtJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * erp公共pai
 * @author hongjiye
 * @time 2017-12-08 10:13
 **/
@Api(description = "erp公共pai")
@RestController
@RequestMapping("/api/erp")
public class ErpController {

	@Autowired
	private ErpService erpService;

	@ApiOperation(value = "获取商家联盟可用的行业erp列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/list/erpModel", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GtJsonResult<List<ErpModelVO>> listErpModel(HttpServletRequest request) throws Exception {
		BusUser busUser = SessionUtils.getLoginUser(request);
		Integer busId = busUser.getId();
		List<ErpModelVO> list = erpService.listErpByBusId(busId);
		return GtJsonResult.instanceSuccessMsg(list);
	}

}