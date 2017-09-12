package com.gt.union.member.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.log.service.IUnionLogErrorService;
import com.gt.union.main.controller.IndexController;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 联盟成员入盟申请 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionMemberJoin")
public class UnionMemberJoinController {
	private Logger logger = LoggerFactory.getLogger(UnionMemberJoinController.class);

	@Autowired
	private IUnionLogErrorService unionLogErrorService;

	/*@ApiOperation(value = "分页查询入盟申请列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String method(HttpServletRequest request) {
	    try {
	        BusUser busUser = SessionUtils.getLoginUser(request);
	        Integer busId = busUser.getId();
	        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
	            busId = busUser.getPid();
	        }
	        return GTJsonResult.instanceSuccessMsg().toString();
	    } catch (BaseException e) {
	        logger.error("", e);
	        this.unionLogErrorService.saveIfNotNull(e);
	        return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
	    } catch (Exception e) {
	        logger.error("", e);
	        this.unionLogErrorService.saveIfNotNull(e);
	        return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
	    }
	}*/
}
