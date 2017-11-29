package com.gt.union.card.activity.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.vo.CardActivityBasicVO;
import com.gt.union.card.activity.vo.CardActivityVO;
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
 * 活动 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Api(description = "活动")
@RestController
@RequestMapping("/unionCardActivity")
public class UnionCardActivityController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页获取已报名且通过的活动信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/pass/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageActivityBasicVOByUnionId(HttpServletRequest request,
                                               Page page,
                                               @ApiParam(value = "联盟id", name = "unionId", required = true)
                                               @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<CardActivityBasicVO> voList = MockUtil.list(CardActivityBasicVO.class, page.getSize());
        page.setRecords(voList);
        return GtJsonResult.instanceSuccessMsg(page).toString();
    }

    @ApiOperation(value = "分页获取活动卡信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageActivityVOByUnionId(HttpServletRequest request,
                                          Page page,
                                          @ApiParam(value = "联盟id", name = "unionId", required = true)
                                          @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<CardActivityVO> voList = MockUtil.list(CardActivityVO.class, page.getSize());
        page.setRecords(voList);
        return GtJsonResult.instanceSuccessMsg(page).toString();
    }

    @ApiOperation(value = "获取联盟卡活动信息", produces = "application/json;charset=UTF-8")
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
        List<UnionCardActivity> result = MockUtil.list(UnionCardActivity.class, 20);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "获取联盟卡活动信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/consume", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listConsumeByUnionId(HttpServletRequest request,
                                       @ApiParam(value = "联盟id", name = "unionId", required = true)
                                       @PathVariable("unionId") Integer unionId,
                                       @ApiParam(value = "联盟卡粉丝Id", name = "fanId", required = true)
                                       @RequestParam(value = "fanId") Integer fanId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionCardActivity> result = MockUtil.list(UnionCardActivity.class, 20);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "保存新建的活动", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveByUnionId(HttpServletRequest request,
                                @ApiParam(value = "联盟id", name = "unionId", required = true)
                                @PathVariable("unionId") Integer unionId,
                                @ApiParam(value = "表单信息", name = "activity", required = true)
                                @RequestBody UnionCardActivity activity) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //------------------------------------------------- delete ---------------------------------------------------------

    @ApiOperation(value = "删除活动", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{activityId}/unionId/{unionId}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    public String removeByIdAndUnionId(HttpServletRequest request,
                                       @ApiParam(value = "活动id", name = "activityId", required = true)
                                       @PathVariable("activityId") Integer activityId,
                                       @ApiParam(value = "联盟id", name = "unionId", required = true)
                                       @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

}