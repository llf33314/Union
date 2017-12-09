package com.gt.union.api.erp.jxc.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.erp.jxc.entity.JxcProductClassPO;
import com.gt.union.api.erp.jxc.entity.JxcProductPO;
import com.gt.union.api.erp.jxc.service.JxcProductClassService;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author hongjiye
 * @time 2017-12-07 17:27
 **/
@Api(description = "进销存商品分类api")
@RestController
@RequestMapping("/jxc/api")
public class JxcProductClassController {

	@Autowired
	private JxcProductClassService jxcProductClassService;

	@ApiOperation(value = "分页：获取商家进销存商品分类", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/list/jxcProductClass", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GtJsonResult<List<JxcProductClassPO>> listJxcProductClass(HttpServletRequest request) throws Exception {
		BusUser busUser = SessionUtils.getLoginUser(request);
		Integer busId = busUser.getId();
		if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
			throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
		}
		List<JxcProductClassPO> list = jxcProductClassService.listProductClassByBusId(busId);
		return GtJsonResult.instanceSuccessMsg(list);
	}
}
