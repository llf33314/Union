package com.gt.union.main.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.main.entity.UnionMainNotice;
import com.gt.union.main.service.IUnionMainNoticeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

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

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "获取联盟公告", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getByUnionId(@ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId) {
        try {
            UnionMainNotice result = this.unionMainNoticeService.getByUnionId(unionId);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    //-------------------------------------------------- put ----------------------------------------------------------

    @ApiOperation(value = "保存联盟公告", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String saveByUnionId(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "noticeContent", value = "联盟公告信息", required = true)
                                @RequestParam @NotNull String noticeContent) {
        try {
            BusUser user = SessionUtils.getLoginUser(request);
            if (CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionMainNoticeService.updateOrSaveByBusIdAndMemberId(user.getId(), memberId, noticeContent);
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    //------------------------------------------------- post ----------------------------------------------------------
}
