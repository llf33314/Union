package com.gt.union.controller.business;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.business.UnionBrokerage;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMemberService;
import com.gt.union.service.business.IUnionBrokerageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
	private IUnionMemberService unionMemberService;

	@Autowired
	private IUnionBrokerageService unionBrokerageService;

	@ApiOperation(value = "获取联盟佣金比例列表", notes = "获取联盟佣金比例列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
	public String listByUnionId(Page page, HttpServletRequest request
		 , @ApiParam(name="unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId){
		try{
            BusUser user = SessionUtils.getLoginUser(request);
            Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			page = unionMemberService.selectUnionBrokerageList(page,unionId,busId);//TODO controller调用对应的service；还有方法名用list
            return GTJsonResult.instanceSuccessMsg(page).toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_UNIONID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}

	@SysLogAnnotation(description = "设置盟员佣金比", op_function = "3")
	@RequestMapping("/{id}/unionId/{unionId}")
	public String updateByIdAndUnionId(HttpServletRequest request
	    , @ApiParam(name="id", value = "佣金比例id", required = true) @PathVariable Integer id
	    , @ApiParam(name="unionId", value = "联盟id", required = true) @PathVariable Integer unionId
	    , @ApiParam(name="unionBrokerage", value = "佣金比例信息", required = true) @RequestBody UnionBrokerage unionBrokerage){
		try{
            BusUser user = SessionUtils.getLoginUser(request);
            Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			unionBrokerage.setFromBusId(busId);
			unionBrokerageService.updateByIdAndUnionId(unionBrokerage);
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
