package com.gt.union.api.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.client.shop.vo.ShopVO;
import com.gt.union.common.response.GtJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 商家门店信息RestApi
 * @author hongjiye
 * @time 2017-11-30 11:03
 **/
@Api(description = "门店信息Api")
@RestController
@RequestMapping("/api/shop")
public class ShopApiController {

	@Autowired
	private ShopService shopService;

	@ApiOperation(value = "获取商家门店列表信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GtJsonResult<List<ShopVO>> listShopByBusId(HttpServletRequest request) throws Exception {
		BusUser busUser = SessionUtils.getLoginUser(request);
		Integer busId = busUser.getId();
		List<ShopVO> list = shopService.listByBusId(busId);
		return GtJsonResult.instanceSuccessMsg(list);
	}


}
