package com.gt.union.main.controller;

import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.entity.BusUser;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.main.entity.UnionMainNotice;
import com.gt.union.main.service.IUnionMainNoticeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 联盟公告 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionMainNotice")
public class UnionMainNoticeController {

	private Logger logger = Logger.getLogger(UnionMainNoticeController.class);

	@Autowired
	private IUnionMainNoticeService unionMainNoticeService;

	@ApiOperation(value = "获取联盟公告", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getByUnionId(@ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId) {
		try {
			UnionMainNotice notice = unionMainNoticeService.getByUnionId(unionId);
			return GTJsonResult.instanceSuccessMsg(notice).toString();
		}catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg("获取联盟公告失败").toString();
		}
	}

	@ApiOperation(value = "保存联盟公告", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String saveByUnionId(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId")Integer unionId
			, @ApiParam(name = "noticeContent", value = "联盟公告信息", required = true) @RequestParam String noticeContent) {
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
				throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
			}
			UnionMainNotice unionNotice = unionMainNoticeService.saveByUnionId(unionId, user.getId(), noticeContent);
			return GTJsonResult.instanceSuccessMsg(unionNotice).toString();
		}catch (BaseException e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg( e.getErrorMsg()).toString();
		}catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}
}
