package com.gt.union.card.project.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.project.entity.UnionCardProjectFlow;
import com.gt.union.card.project.service.IUnionCardProjectFlowService;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 活动项目流程 前端控制器
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
@Api(description = "活动项目流程")
@RestController
@RequestMapping("/unionActivityFlow")
public class UnionCardProjectFlowController {

    @Autowired
    private IUnionCardProjectFlowService unionCardProjectFlowService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "联盟卡设置-活动卡设置-分页-我的活动项目-审批记录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/projectId/{projectId}/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listByProjectIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "活动id", name = "activityId", required = true)
            @PathVariable("activityId") Integer activityId,
            @ApiParam(value = "项目id", name = "projectId", required = true)
            @PathVariable("projectId") Integer projectId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionCardProjectFlow> result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.list(UnionCardProjectFlow.class, 20);
        } else {
            result = unionCardProjectFlowService.listByBusIdAndUnionIdAndActivityIdAndProjectId(busId, unionId, activityId, projectId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

}