package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.basic.UnionNotice;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionNoticeService;
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
 * 联盟公告 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@RestController
@RequestMapping("/unionNotice")
public class UnionNoticeController {
	private static final String GET_UNIONID = "UnionNoticeController.getByUnionId()";
	private static final String SAVE_UNIONID = "UnionNoticeController.saveByUnionId()";

	private Logger logger = Logger.getLogger(UnionNoticeController.class);

	@Autowired
	private IUnionNoticeService unionNoticeService;

	@ApiOperation(value = "获取联盟公告", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getByUnionId(@ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId) {
		try {
			UnionNotice notice = unionNoticeService.getByUnionId(unionId);
			return GTJsonResult.instanceSuccessMsg(notice).toString();
		}catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(GET_UNIONID,"获取联盟公告失败").toString();
		}
	}

	@ApiOperation(value = "保存联盟公告", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String saveByUnionId(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId")Integer unionId
        , @ApiParam(name = "noticeContent", value = "联盟公告信息", required = true) @RequestParam String noticeContent) {
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
				throw new BusinessException(SAVE_UNIONID, "", CommonConstant.UNION_BUS_PARENT_MSG);
			}
            UnionNotice unionNotice = unionNoticeService.saveByUnionId(unionId, user.getId(), noticeContent);
            return GTJsonResult.instanceSuccessMsg(unionNotice).toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(SAVE_UNIONID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}


}
