package com.gt.union.api.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.TCommonStaff;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.staff.ITCommonStaffService;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author hongjiye
 * @time 2017-12-13 11:30
 **/
@Api(description = "员工pai")
@RestController
@RequestMapping("/api/staff")
public class StaffApiController {

	@Autowired
	private ITCommonStaffService commonStaffService;

	@ApiOperation(value = "根据门店列表获取员工列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/list/{shopId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GtJsonResult<List<TCommonStaff>> listStaffByShopId(HttpServletRequest request,
					 @ApiParam(value = "门店id", name = "shopId", required = true)
					 @PathVariable(value = "shopId") Integer shopId) throws Exception {
		BusUser busUser = SessionUtils.getLoginUser(request);
		List<TCommonStaff> list = commonStaffService.listTCommonStaffByShopId(shopId, busUser.getId());
		return GtJsonResult.instanceSuccessMsg(list);
	}

}
