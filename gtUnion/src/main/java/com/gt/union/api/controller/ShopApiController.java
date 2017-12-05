package com.gt.union.api.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.amqp.entity.PhoneMessage;
import com.gt.union.api.amqp.sender.PhoneMessageSender;
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
 * 门店信息RestApi
 * @author hongjiye
 * @time 2017-11-30 11:03
 **/
@Api(description = "门店信息RestApi")
@RestController
@RequestMapping("/api/shop")
public class ShopApiController {

	@ApiParam
	private ShopService shopService;


	@Autowired
	private PhoneMessageSender sender;

	@ApiOperation(value = "获取商家门店列表信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GtJsonResult<List<ShopVO>> getBasicVOById(HttpServletRequest request) throws Exception {
		BusUser busUser = SessionUtils.getLoginUser(request);
		Integer busId = busUser.getId();
		List<ShopVO> list = shopService.listByBusId(busId);
		return GtJsonResult.instanceSuccessMsg(list);
	}

	@ApiOperation(value = "获取门店列表信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/sms", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void sms(HttpServletRequest request) throws Exception {

		sender.sendMsg(new PhoneMessage(33,"15986670850","哈ffasd23153f哈哈"));
	}
}
