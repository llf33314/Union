package com.gt.union.preferential.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.log.service.IUnionLogErrorService;
import com.gt.union.preferential.constant.PreferentialConstant;
import com.gt.union.preferential.service.IUnionPreferentialProjectService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 优惠项目 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionPreferentialProject")
public class UnionPreferentialProjectController {
    private Logger logger = LoggerFactory.getLogger(UnionPreferentialProjectController.class);

    @Autowired
    private IUnionLogErrorService unionLogErrorService;

    @Autowired
    private IUnionPreferentialProjectService unionPreferentialProjectService;

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "查询我的优惠项目", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/myProject/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getByMemberId(HttpServletRequest request, Page page
            , @ApiParam(name = "memberId", value = "操作者的盟员身份id", required = true)
                                @PathVariable("memberId") Integer memberId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            Map<String, Object> result = this.unionPreferentialProjectService.getPageMapByBusIdAndMemberId(page, busId, memberId);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    @ApiOperation(value = "根据审核状态，分页查询优惠项目列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/page/memberId/{memberId}/status/{status}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageMapByMemberIdAndItemStatus(HttpServletRequest request, Page page
            , @ApiParam(name = "memberId", value = "操作者的盟员身份id", required = true)
                                                 @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "status", value = "优惠服务项审核状态，1是未提交，2是审核中，3是审核通过，4是审核不通过", required = true)
                                                 @PathVariable("status") Integer status) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            Page pageData = this.unionPreferentialProjectService.pageMapByBusIdAndMemberIdAndItemStatus(page, busId, memberId, status);
            Integer unCommittedCount = this.unionPreferentialProjectService.countByBusInAndMemberIdAndItemStatus(busId
                    , memberId, PreferentialConstant.STATUS_UNCOMMITTED);
            Integer verifyingCount = this.unionPreferentialProjectService.countByBusInAndMemberIdAndItemStatus(busId
                    , memberId, PreferentialConstant.STATUS_VERIFYING);
            Integer passCount = this.unionPreferentialProjectService.countByBusInAndMemberIdAndItemStatus(busId
                    , memberId, PreferentialConstant.STATUS_PASS);
            Integer failCount = this.unionPreferentialProjectService.countByBusInAndMemberIdAndItemStatus(busId
                    , memberId, PreferentialConstant.STATUS_FAIL);
            Map<String, Object> result = new HashMap<>();
            result.put("pageData", pageData);
            result.put("unCommittedCount", unCommittedCount);
            result.put("verifyingCount", verifyingCount);
            result.put("passCount", passCount);
            result.put("failCount", failCount);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    @ApiOperation(value = "根据优惠项目id和审核状态，查询详情信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{projectId}/memberId/{memberId}/status/{status}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getDetailByMemberIdAndProjectIdAndItemStatus(HttpServletRequest request
            , @ApiParam(name = "projectId", value = "优惠项目id", required = true)
                                                               @PathVariable("projectId") Integer projectId
            , @ApiParam(name = "memberId", value = "操作者的盟员身份id", required = true)
                                                               @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "status", value = "优惠服务项审核状态，2是审核中，3是审核通过，4是审核不通过", required = true)
                                                               @PathVariable("status") Integer status) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            Map<String, Object> result = this.unionPreferentialProjectService.getDetailByBusIdAndMemberIdAndProjectIdAndItemStatus(busId, memberId, projectId, status);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    //-------------------------------------------------- put ----------------------------------------------------------

    @ApiOperation(value = "更新优惠项目说明", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{projectId}/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateIllustrationByIdAndMemberId(HttpServletRequest request
            , @ApiParam(name = "projectId", value = "优惠项目id", required = true)
                                                    @PathVariable("projectId") Integer projectId
            , @ApiParam(name = "memberId", value = "操作者的盟员身份id", required = true)
                                                    @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "illustration", value = "优惠项目说明", required = true)
                                                    @RequestBody @NotNull String illustration) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionPreferentialProjectService.updateIllustrationByIdAndBusIdAndMemberId(projectId, busUser.getId(), memberId, illustration);
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
    }

    //------------------------------------------------- post ----------------------------------------------------------

}
