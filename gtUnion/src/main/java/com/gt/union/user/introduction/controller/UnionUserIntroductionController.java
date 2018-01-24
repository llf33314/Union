package com.gt.union.user.introduction.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.union.main.vo.UnionVO;
import com.gt.union.user.introduction.entity.UnionUserIntroduction;
import com.gt.union.user.introduction.service.IUnionUserIntroductionService;
import com.gt.union.user.introduction.vo.UnionUserIntroductionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 联盟商家简介 前端控制器
 *
 * @author linweicong
 * @version 2018-01-24 16:24:13
 */
@Api(description = "联盟商家简介")
@RestController
@RequestMapping("/unionUserIntroduction")
public class UnionUserIntroductionController {

	@Autowired
	private IUnionUserIntroductionService unionUserIntroductionService;

    //-------------------------------------------------- get -----------------------------------------------------------
	@ApiOperation(value = "商家简介信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getUnionMainVOById(
			HttpServletRequest request) throws Exception {
		BusUser busUser = SessionUtils.getLoginUser(request);
		Integer busId = busUser.getId();
		if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
			busId = busUser.getPid();
		}
		List<UnionUserIntroduction> result = unionUserIntroductionService.listValidByBusId(busId);
		return GtJsonResult.instanceSuccessMsg(result).toString();
	}

    //-------------------------------------------------- put -----------------------------------------------------------

	@ApiOperation(value = "商家简介信息-保存", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public String updateUnionMainVOById(
			HttpServletRequest request,
			@ApiParam(value = "表单信息", name = "unionUserIntroductionVO", required = true)
			@RequestBody UnionUserIntroductionVO unionUserIntroductionVO) throws Exception {
		BusUser busUser = SessionUtils.getLoginUser(request);
		Integer busId = busUser.getId();
		if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
			busId = busUser.getPid();
		}
		unionUserIntroductionService.saveOrUpdate(busId, unionUserIntroductionVO);
		return GtJsonResult.instanceSuccessMsg().toString();
	}
    //-------------------------------------------------- post ----------------------------------------------------------

}