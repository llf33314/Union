package com.gt.union.card.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.entity.UnionActivityProject;
import com.gt.union.card.vo.ActivityProjectErpVO;
import com.gt.union.card.vo.ActivityProjectIndexVO;
import com.gt.union.card.vo.ActivityProjectVO;
import com.gt.union.card.vo.Project;
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
 * 活动项目 前端控制器
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
@Api(description = "活动项目")
@RestController
@RequestMapping("/unionActivityProject")
public class UnionActivityProjectController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页获取活动项目", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageByUnionId(HttpServletRequest request,
                                Page page,
                                @ApiParam(value = "联盟id", name = "unionId", required = true)
                                @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<ActivityProjectIndexVO> voList = MockUtil.list(ActivityProjectIndexVO.class, page.getSize());
        page.setRecords(voList);
        return GtJsonResult.instanceSuccessMsg(page).toString();
    }

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
        List<UnionActivityProject> result = MockUtil.list(UnionActivityProject.class, 30);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "根据联盟卡活动id和联盟id，获取参与盟员数信息", produces = "application/json;charset=UTF-8")
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
        List<ActivityProjectVO> result = MockUtil.list(ActivityProjectVO.class, 20);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "根据联盟卡活动id和联盟id，获取项目审核信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}/projectCheck", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listProjectCheckByActivityIdAndUnionId(HttpServletRequest request,
                                                         @ApiParam(value = "联盟卡活动id", name = "activityId", required = true)
                                                         @PathVariable("activityId") Integer activityId,
                                                         @ApiParam(value = "联盟id", name = "unionId", required = true)
                                                         @PathVariable("unionId") Integer unionId) throws Exception {
//        BusUser busUser = SessionUtils.getLoginUser(request);
//        Integer busId = busUser.getId();
//        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
//            busId = busUser.getPid();
//        }
        // mock
        List<ActivityProjectVO> result = MockUtil.list(ActivityProjectVO.class, 20);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "根据活动项目id和联盟id，获取我的活动项目", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{activityProjectId}/unionId/{unionId}/project", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getByIdAndUnionId(HttpServletRequest request,
                                    @ApiParam(value = "活动项目id", name = "activityProjectId", required = true)
                                    @PathVariable("activityProjectId") Integer activityProjectId,
                                    @ApiParam(value = "联盟id", name = "unionId", required = true)
                                    @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        ActivityProjectErpVO result = MockUtil.get(ActivityProjectErpVO.class);
        List<Project> textList = MockUtil.list(Project.class, 20);
        result.setProjectTextList(textList);
        List<Project> erpTextList = MockUtil.list(Project.class, 20);
        result.setProjectErpTextList(erpTextList);
        List<Project> erpGoodsList = MockUtil.list(Project.class, 20);
        result.setProjectErpGoodsList(erpGoodsList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "审核项目", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/projectCheck", method = RequestMethod.PATCH, produces = "application/json;charset=UTF-8")
    public String updateProjectCheck(HttpServletRequest request,
                                     @ApiParam(value = "是否通过(0:否 1:是)", name = "isPass", required = true)
                                     @RequestParam(value = "isPass") Integer isPass,
                                     @ApiParam(value = "活动项目id列表", name = "activityProjectIdList", required = true)
                                     @RequestBody List<Integer> activityProjectIdList) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

}