package com.gt.union.card.project.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.project.vo.CardProjectItemConsumeVO;
import com.gt.union.card.project.vo.CardProjectItemVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 项目优惠 前端控制器
 *
 * @author linweicong
 * @version 2017-11-27 09:55:47
 */
@Api(description = "项目优惠")
@RestController
@RequestMapping("/unionCardProjectItem")
public class UnionCardProjectItemController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "获取活动项目优惠列表", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}/consume", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listConsumeByActivityIdAndUnionId(HttpServletRequest request,
                                                    @ApiParam(value = "活动id", name = "activityId", required = true)
                                                    @PathVariable("activityId") Integer activityId,
                                                    @ApiParam(value = "联盟id", name = "unionId", required = true)
                                                    @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<CardProjectItemConsumeVO> result = MockUtil.list(CardProjectItemConsumeVO.class, 20);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "提交审核项目优惠", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/projectId/{projectId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateByProjectId(HttpServletRequest request,
                                    @ApiParam(value = "项目id", name = "projectId", required = true)
                                    @PathVariable("projectId") Integer projectId,
                                    @ApiParam(value = "表单信息", name = "cardProjectItemVO", required = true)
                                    @RequestBody CardProjectItemVO cardProjectItemVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "保存项目优惠", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/projectId/{projectId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveByProjectId(HttpServletRequest request,
                                  @ApiParam(value = "项目id", name = "projectId", required = true)
                                  @PathVariable("projectId") Integer projectId,
                                  @ApiParam(value = "表单信息", name = "cardProjectItemVO", required = true)
                                  @RequestBody CardProjectItemVO cardProjectItemVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }
}