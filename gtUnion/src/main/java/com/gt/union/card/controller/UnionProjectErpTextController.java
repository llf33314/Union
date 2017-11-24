package com.gt.union.card.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.vo.ActivityProjectErpVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * ERP文本项目 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Api(description = "ERP文本项目")
@RestController
@RequestMapping("/unionProjectErpText")
public class UnionProjectErpTextController {

    //-------------------------------------------------- get -----------------------------------------------------------

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "根据活动项目id和表单信息，保存并提交Erp活动项目", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityProjectId/{activityProjectId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String saveAndCommit(HttpServletRequest request,
                                @ApiParam(value = "活动项目id", name = "activityProjectId", required = true)
                                @PathVariable("activityProjectId") Integer activityProjectId,
                                @ApiParam(value = "表单信息", name = "activityProjectErpVO", required = true)
                                @RequestBody ActivityProjectErpVO activityProjectErpVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "根据活动项目id和表单信息，保存Erp活动项目", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityProjectId/{activityProjectId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String save(HttpServletRequest request,
                       @ApiParam(value = "活动项目id", name = "activityProjectId", required = true)
                       @PathVariable("activityProjectId") Integer activityProjectId,
                       @ApiParam(value = "表单信息", name = "activityProjectErpVO", required = true)
                       @RequestBody ActivityProjectErpVO activityProjectErpVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

}