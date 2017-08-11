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
import org.springframework.stereotype.Controller;
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
	//TODO 方法签名RESTFUL化
	private static final String UNION_BROKERAGE = "UnionBrokerageController.unionBrokerage()";
	private static final String UPDATE_UNION_BROKERAGE = "UnionBrokerageController.updateUnionBrokerage()";
	private Logger logger = Logger.getLogger(UnionBusinessRecommendController.class);

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private IUnionBrokerageService unionBrokerageService;


	/**
	 * 获取联盟佣金比例列表
	 * @param page
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "获取联盟佣金比例列表", notes = "获取联盟佣金比例列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
	public String unionBrokerage(Page page, HttpServletRequest request,
								 @ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = true) Integer unionId){
		try{
            BusUser user = SessionUtils.getLoginUser(request); //TODO 没有校验user是否为空
            Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			page = unionMemberService.selectUnionBrokerageList(page,unionId,busId);
            return GTJsonResult.instanceSuccessMsg(page).toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UNION_BROKERAGE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}

	/**
	 *	设置盟员佣金比
	 * @param request
	 * @param id	盟员id
	 * @param unionBrokerage
	 * @return
	 */
	@SysLogAnnotation(description = "设置盟员佣金比", op_function = "3")
	@RequestMapping("/{id}")
	public String updateUnionBrokerage(HttpServletRequest request,
					   @ApiParam(name="id", value = "佣金比例id", required = true) @PathVariable Integer id,
					   @ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam Integer unionId,
					   @ApiParam(name="unionBrokerage", value = "佣金比例信息", required = true) @RequestBody UnionBrokerage unionBrokerage){
		try{
            BusUser user = SessionUtils.getLoginUser(request);//TODO 没有校验user是否为空
            Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			unionBrokerage.setFromBusId(busId);
			unionBrokerageService.updateUnionBrokerage(unionBrokerage);
            return GTJsonResult.instanceSuccessMsg().toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_UNION_BROKERAGE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}
}
