package com.gt.union.api.erp.jxc.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.erp.jxc.entity.JxcProduct;
import com.gt.union.api.erp.jxc.service.JxcProductService;
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
 * @time 2017-12-07 17:18
 **/
@Api(description = "进销存商品api")
@RestController
@RequestMapping("/jxc/api")
public class JxcProductController {

	@Autowired
	private JxcProductService jxcProductService;

	@ApiOperation(value = "分页：获取商家进销存商品信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/list/jxcProduct", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GtJsonResult<Page<List<JxcProduct>>> listJxcProductPage(
			HttpServletRequest request, Page page,
			@ApiParam(value = "门店id", name = "shopId", required = true)
			@RequestParam(value = "shopId") Integer shopId,
			@ApiParam(value = "商品分类id", name = "classId")
			@RequestParam(value = "classId", required = false) Integer classId,
			@ApiParam(value = "搜索条件(名称/条码/编码/全拼码)", name = "search")
			@RequestParam(value = "search", required = false) String search) throws Exception {
		BusUser busUser = SessionUtils.getLoginUser(request);
		Page<List<JxcProduct>> resultPage = jxcProductService.listProductByShopIdAndClassIdAndSearchPage(shopId, classId, search, page.getCurrent(), page.getSize());
		return GtJsonResult.instanceSuccessMsg(resultPage);
	}


}
