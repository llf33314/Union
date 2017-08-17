package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.basic.UnionMemberPreferentialManager;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMemberPreferentialServiceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 盟员优惠服务项 前端 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@RestController
@RequestMapping("/unionMemberPreferentialService")
public class UnionMemberPreferentialServiceController {

	private static final String SAVE = "UnionMemberPreferentialServiceController.save()";
	private static final String DELETE = "UnionMemberPreferentialServiceController.delete()";
	private Logger logger = LoggerFactory.getLogger(UnionMemberPreferentialServiceController.class);

	@Autowired
	private IUnionMemberPreferentialServiceService unionMemberPreferentialServiceService;

	@ApiOperation(value = "删除我的优惠项目", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "4", description = "删除我的优惠项目")
	@RequestMapping(value = "/{id}/unionId/{unionId}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
	public String listMyByUnionId(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable(name = "unionId", required = true) Integer unionId
								  ,@ApiParam(name = "id", value = "项目id", required = true) @PathVariable(name = "id", required = true) Integer id) {
		try {
			unionMemberPreferentialServiceService.delete(unionId, id);
			return GTJsonResult.instanceSuccessMsg().toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(DELETE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
		}
	}

	@ApiOperation(value = "保存我的优惠项目", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "2", description = "保存我的优惠项目")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String save(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable(name = "unionId", required = true) Integer unionId
			, @ApiParam(name = "serviceName", value = "项目名称", required = true) @RequestParam(name = "serviceName", required = true) String serviceName) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			this.unionMemberPreferentialServiceService.save(unionId, busUser.getId(), serviceName);
			return GTJsonResult.instanceSuccessMsg().toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(SAVE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
		}
	}

}
