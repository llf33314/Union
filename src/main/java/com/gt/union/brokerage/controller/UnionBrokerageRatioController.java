package com.gt.union.brokerage.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.brokerage.service.IUnionBrokerageRatioService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 商机佣金比率 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionBrokerageRatio")
public class UnionBrokerageRatioController {

	private Logger logger = Logger.getLogger(UnionBrokerageRatioController.class);

	@Autowired
	private IUnionBrokerageRatioService unionBrokerageRatioService;

	@ApiOperation(value = "商机佣金比列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String pageBrokerageRatio(HttpServletRequest request, Page page,
						  @ApiParam(name = "unionId", value = "所属联盟id", required = true)
						  @RequestParam(name = "unionId", required = true) Integer unionId) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Integer busId = busUser.getId();
			if(busUser.getPid() != null && busUser.getPid() != 0){
				busId = busUser.getPid();
			}
			Page result = this.unionBrokerageRatioService.pageBrokerageRatio(page, busId, unionId);
			return GTJsonResult.instanceSuccessMsg(result).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "设置佣金比", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String saveOrUpdateBrokerageRatio(HttpServletRequest request,
									 @ApiParam(name = "fromMemberId", value = "设置的盟员id", required = true)
									 @RequestParam(name = "fromMemberId", required = false) Integer fromMemberId
									,@ApiParam(name = "toMemberId", value = "被设置的盟员id", required = true)
									@RequestParam(name = "toMemberId", required = true) Integer toMemberId
									,@ApiParam(name = "ratio", value = "设置的佣金比", required = true)
									 @RequestParam(name = "ratio", required = true) Double ratio) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Integer busId = busUser.getId();
			if(busUser.getPid() != null && busUser.getPid() != 0){
				busId = busUser.getPid();
			}
			this.unionBrokerageRatioService.saveOrUpdateBrokerageRatio(fromMemberId, busId, toMemberId, ratio);
			return GTJsonResult.instanceSuccessMsg().toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	
}
