package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMemberPreferentialServiceService;
import com.gt.union.service.common.IUnionRootService;
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
	private static final String VERIFY = "UnionMemberPreferentialServiceController.verify()";
	private static final String ADD_SERVICE_VERIFY = "UnionMemberPreferentialServiceController.addServiceVerify()";
	private static final String LIST_MY_BY_UNIONID = "UnionMemberPreferentialServiceController.listMyByUnionId()";
	private Logger logger = LoggerFactory.getLogger(UnionMemberPreferentialServiceController.class);

	@Autowired
	private IUnionMemberPreferentialServiceService unionMemberPreferentialServiceService;

	@Autowired
	private IUnionRootService unionRootService;

	@ApiOperation(value = "查询我的优惠项目", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listMyByUnionId(Page page, HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable(name = "unionId", required = true) Integer unionId
			) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Integer busId = busUser.getId();
			if(CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0){
				busId = busUser.getPid();
			}
			Page result = this.unionMemberPreferentialServiceService.listMyByUnionId(page,unionId, busId);
			return GTJsonResult.instanceSuccessMsg(result).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(LIST_MY_BY_UNIONID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
		}
	}

	@ApiOperation(value = "保存我的优惠项目", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "2", description = "保存我的优惠项目")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String save(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable(name = "unionId", required = true) Integer unionId
			, @ApiParam(name = "serviceName", value = "项目名称", required = true) @RequestParam(name = "serviceName", required = true) String serviceName) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			if(CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0){
				throw new BusinessException(SAVE, "", CommonConstant.UNION_BUS_PARENT_MSG);
			}
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

	@ApiOperation(value = "删除我的优惠项目，可批量删除，用逗号隔开", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "4", description = "删除我的优惠项目")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
	public String listMyByUnionId(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable(name = "unionId", required = true) Integer unionId
			,@ApiParam(name = "ids", value = "项目ids", required = true) @RequestParam(name = "ids", required = true) String ids) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			if(CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0){
				throw new BusinessException(SAVE, "", CommonConstant.UNION_BUS_PARENT_MSG);
			}
			if(!unionRootService.checkUnionMainValid(unionId)){
				throw new BusinessException(SAVE, "", CommonConstant.UNION_OVERDUE_MSG);
			}
			if(!unionRootService.hasUnionMemberAuthority(unionId,busUser.getId())){
				throw new BusinessException(SAVE, "", CommonConstant.UNION_MEMBER_NON_AUTHORITY_MSG);
			}
			unionMemberPreferentialServiceService.delete(unionId, ids);
			return GTJsonResult.instanceSuccessMsg().toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(DELETE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
		}
	}

	@ApiOperation(value = "优惠项目提交审核", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "3", description = "优惠项目提交审核")
	@RequestMapping(value = "/{id}/unionId/{unionId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String addServiceVerify(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable(name = "unionId", required = true) Integer unionId
			,@ApiParam(name = "id", value = "项目id", required = true) @PathVariable(name = "id", required = true) Integer id) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			if(CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0){
				throw new BusinessException(SAVE, "", CommonConstant.UNION_BUS_PARENT_MSG);
			}
			if(!unionRootService.checkUnionMainValid(unionId)){
				throw new BusinessException(SAVE, "", CommonConstant.UNION_OVERDUE_MSG);
			}
			if(!unionRootService.hasUnionMemberAuthority(unionId,busUser.getId())){
				throw new BusinessException(SAVE, "", CommonConstant.UNION_MEMBER_NON_AUTHORITY_MSG);
			}
			unionMemberPreferentialServiceService.addServiceVerify(unionId, id);
			return GTJsonResult.instanceSuccessMsg().toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(ADD_SERVICE_VERIFY, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
		}
	}


	@ApiOperation(value = "盟主审核优惠项目，可批量审核， 用逗号隔开", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "3", description = "盟主审核优惠项目")
	@RequestMapping(value = "unionId/{unionId}/verifyStatus/{verifyStatus}", method = RequestMethod.PATCH, produces = "application/json;charset=UTF-8")
	public String verify(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable(name = "unionId", required = true) Integer unionId
			,@ApiParam(name = "ids", value = "项目ids", required = true) @RequestParam(name = "ids", required = true) String ids
			,@ApiParam(name = "verifyStatus", value = "审核状态 2：通过 3：不通过", required = true) @PathVariable(name = "verifyStatus", required = true) Integer verifyStatus
						 ) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			if(CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0){
				throw new BusinessException(VERIFY, "", CommonConstant.UNION_BUS_PARENT_MSG);
			}
			unionMemberPreferentialServiceService.verify(unionId, busUser.getId(), ids, verifyStatus);
			return GTJsonResult.instanceSuccessMsg().toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(VERIFY, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
		}
	}

}
