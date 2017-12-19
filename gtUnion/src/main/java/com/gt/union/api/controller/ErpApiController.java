package com.gt.union.api.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.erp.ErpService;
import com.gt.union.api.client.erp.vo.ErpModelVO;
import com.gt.union.api.client.erp.vo.ErpServerVO;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.client.shop.vo.ShopVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
public class ErpApiController {

	@Autowired
	private ErpService erpService;

	@ApiOperation(value = "获取商家联盟可用的行业erp列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/list/erpModel", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GtJsonResult<List<ErpModelVO>> listErpModel(HttpServletRequest request) throws Exception {
		BusUser busUser = SessionUtils.getLoginUser(request);
		if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
			throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
		}
		Integer busId = busUser.getId();
		List<ErpModelVO> list = erpService.listErpByBusId(busId);
		return GtJsonResult.instanceSuccessMsg(list);
	}

	@ApiOperation(value = "获取erp服务项目列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/list/server/{erpModel}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GtJsonResult<List<ErpServerVO>> listErpServer(Page page, HttpServletRequest request,
					@ApiParam(value = "erp行业类型", name = "erpModel", required = true)
					@PathVariable(value = "erpModel") Integer erpModel,
					@ApiParam(value = "门店id", name = "shopId", required = true)
					@RequestParam(value = "shopId") Integer shopId,
					@ApiParam(value = "搜索条件", name = "search", required = false)
					@RequestParam(value = "search", required = false) String search) throws Exception {
		BusUser busUser = SessionUtils.getLoginUser(request);
		if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
			throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
		}
		List<ErpServerVO> list = erpService.listErpServer(shopId, erpModel, search, page, busUser.getId());
		return GtJsonResult.instanceSuccessMsg(list);
	}


}
