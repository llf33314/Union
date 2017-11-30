package com.gt.union.card.project.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.project.entity.UnionCardProjectFlow;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "获取我的活动项目-审批记录信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/projectId/{projectId}/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<List<UnionCardProjectFlow>> listByProjectIdAndUnionId(
            HttpServletRequest request,
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
        List<UnionCardProjectFlow> result = MockUtil.list(UnionCardProjectFlow.class, 20);
        return GtJsonResult.instanceSuccessMsg(result)
                ;
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

}