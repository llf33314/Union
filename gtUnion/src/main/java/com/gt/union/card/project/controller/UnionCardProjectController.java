package com.gt.union.card.project.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.vo.CardProjectItemVO;
import com.gt.union.card.project.vo.CardProjectVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 活动项目 前端控制器
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
@Api(description = "活动项目")
@RestController
@RequestMapping("/unionCardProject")
public class UnionCardProjectController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "获取所有活动项目", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listByUnionId(HttpServletRequest request,
                                @ApiParam(value = "联盟id", name = "unionId", required = true)
                                @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionCardProject> result = MockUtil.list(UnionCardProject.class, 30);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "获取参与盟员数信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}/joinMember", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listJoinMemberByActivityIdAndUnionId(HttpServletRequest request,
                                                       @ApiParam(value = "联盟卡活动id", name = "activityId", required = true)
                                                       @PathVariable("activityId") Integer activityId,
                                                       @ApiParam(value = "联盟id", name = "unionId", required = true)
                                                       @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<CardProjectVO> result = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            CardProjectVO vo = MockUtil.get(CardProjectVO.class);
            List<UnionCardProjectItem> itemList = MockUtil.list(UnionCardProjectItem.class, 3);
            vo.setItemList(itemList);
            result.add(vo);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "获取项目审核信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}/projectCheck", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listProjectCheckByActivityIdAndUnionId(HttpServletRequest request,
                                                         @ApiParam(value = "联盟卡活动id", name = "activityId", required = true)
                                                         @PathVariable("activityId") Integer activityId,
                                                         @ApiParam(value = "联盟id", name = "unionId", required = true)
                                                         @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<CardProjectVO> result = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            CardProjectVO vo = MockUtil.get(CardProjectVO.class);
            List<UnionCardProjectItem> itemList = MockUtil.list(UnionCardProjectItem.class, 3);
            vo.setItemList(itemList);
            result.add(vo);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "获取我的活动项目", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{projectId}/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getByIdAndUnionId(HttpServletRequest request,
                                    @ApiParam(value = "活动项目id", name = "projectId", required = true)
                                    @PathVariable("projectId") Integer projectId,
                                    @ApiParam(value = "联盟id", name = "unionId", required = true)
                                    @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        CardProjectItemVO result = MockUtil.get(CardProjectItemVO.class);
        List<UnionCardProjectItem> nonErpTextList = MockUtil.list(UnionCardProjectItem.class, 10);
        result.setNonErpTextList(nonErpTextList);
        List<UnionCardProjectItem> erpTextList = MockUtil.list(UnionCardProjectItem.class, 10);
        result.setErpTextList(erpTextList);
        List<UnionCardProjectItem> erpGoodsList = MockUtil.list(UnionCardProjectItem.class, 10);
        result.setErpGoodsList(erpGoodsList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }
    
    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "审核项目", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/projectCheck", method = RequestMethod.PATCH, produces = "application/json;charset=UTF-8")
    public String updateProjectCheck(HttpServletRequest request,
                                     @ApiParam(value = "是否通过(0:否 1:是)", name = "isPass", required = true)
                                     @RequestParam(value = "isPass") Integer isPass,
                                     @ApiParam(value = "活动项目id列表", name = "projectIdList", required = true)
                                     @RequestBody List<Integer> projectIdList) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

}