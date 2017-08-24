package com.gt.union.controller.business;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.business.vo.UnionBrokerageVO;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.business.IUnionBrokerageService;
import com.gt.union.service.common.IUnionValidateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>
 * 联盟商家佣金比率 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@RestController
@RequestMapping("/unionBrokerage")
public class UnionBrokerageController {
	private static final String LIST_UNIONID = "UnionBrokerageController.listByUnionId()";
	private static final String UPDATE_ID_UNIONID = "UnionBrokerageController.updateByIdAndUnionId()";
	private Logger logger = Logger.getLogger(UnionBusinessRecommendController.class);

	@Autowired
	private IUnionValidateService unionValidateService;

	@Autowired
	private IUnionBrokerageService unionBrokerageService;

	@ApiOperation(value = "获取联盟佣金比例列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
	public String listByUnionId(Page page, HttpServletRequest request
		 , @ApiParam(name="unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId){
		try{
            BusUser user = SessionUtils.getLoginUser(request);
            Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			page = this.unionBrokerageService.listMapByUnionIdAndFromBusId(page, unionId, busId);
            return GTJsonResult.instanceSuccessMsg(page).toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_UNIONID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}

	@SysLogAnnotation(description = "商机佣金比例设置", op_function = "3")
	@ApiOperation(value = "商机佣金比例设置", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public String updateByIdAndUnionId(HttpServletRequest request
	    , @ApiParam(name="unionBrokerageVO", value = "商机佣金比例参数实体", required = true)
        @RequestBody @Valid UnionBrokerageVO unionBrokerageVO, BindingResult bindingResult){
		try{
		    this.unionValidateService.checkBindingResult(bindingResult);
            BusUser user = SessionUtils.getLoginUser(request);
            Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}

			this.unionBrokerageService.updateOrSave(unionBrokerageVO, busId);
            return GTJsonResult.instanceSuccessMsg().toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_ID_UNIONID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}
}
