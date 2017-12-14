package com.gt.union.card.activity.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.activity.vo.*;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IUnionCardActivityService unionCardActivityService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "售卡佣金分成管理-活动卡售卡比例设置-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/sharingRatio/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<CardActivityStatusVO>> pageActivityStatusVOByUnionId(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
//        List<CardActivityStatusVO> voList = MockUtil.list(CardActivityStatusVO.class, page.getSize());
        List<CardActivityStatusVO> voList = unionCardActivityService.listCardActivityStatusVOByBusIdAndUnionId(busId, unionId);
        Page<CardActivityStatusVO> result = (Page<CardActivityStatusVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "联盟卡设置-活动卡设置-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<CardActivityVO>> pageActivityVOByUnionId(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
//        List<CardActivityVO> voList = MockUtil.list(CardActivityVO.class, page.getSize());
        List<CardActivityVO> voList = unionCardActivityService.listCardActivityVOByBusIdAndUnionId(busId, unionId);
        Page<CardActivityVO> result = (Page<CardActivityVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "前台-联盟卡消费核销-开启优惠项目-查询活动卡列表", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/consume", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<List<CardActivityConsumeVO>> listCardActivityConsumeVOByUnionIdAndFanId(
            HttpServletRequest request,
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
//        List<CardActivityConsumeVO> result = MockUtil.list(CardActivityConsumeVO.class, 20);
        List<CardActivityConsumeVO> result = unionCardActivityService.listCardActivityConsumeVOByBusIdAndUnionIdAndFanId(busId, unionId, fanId);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "前台-办理联盟卡-查询联盟和联盟卡-查询联盟卡活动", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{activityId}/unionId/{unionId}/apply", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<CardActivityApplyVO> getApplyVOByIdAndUnionId(
            HttpServletRequest request,
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
//        CardActivityApplyVO result = MockUtil.get(CardActivityApplyVO.class);
        CardActivityApplyVO result = unionCardActivityService.getCardActivityApplyVOByBusIdAndIdAndUnionId(busId, activityId, unionId);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "前台-办理联盟卡-查询联盟和联盟卡-查询联盟卡活动-查询服务项目数", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{activityId}/unionId/{unionId}/apply/itemCount", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<List<CardActivityApplyItemVO>> getApplyItemVOByIdAndUnionId(
            HttpServletRequest request,
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
//        List<CardActivityApplyItemVO> result = MockUtil.list(CardActivityApplyItemVO.class, 20);
        List<CardActivityApplyItemVO> result = unionCardActivityService.listCardActivityApplyItemVOByBusIdAndIdAndUnionId(busId, activityId, unionId);
        return GtJsonResult.instanceSuccessMsg(result);
    }


    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "联盟卡设置-活动卡设置-新增活动卡", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> saveByUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "表单信息", name = "activity", required = true)
            @RequestBody UnionCardActivity vo) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        unionCardActivityService.saveByBusIdAndUnionId(busId, unionId, vo);
        return GtJsonResult.instanceSuccessMsg();
    }

    //------------------------------------------------- delete ---------------------------------------------------------

    @ApiOperation(value = "联盟卡设置-活动卡设置-分页-删除", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{activityId}/unionId/{unionId}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> removeByIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "活动id", name = "activityId", required = true)
            @PathVariable("activityId") Integer activityId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        unionCardActivityService.removeByBusIdAndIdAndUnionId(busId, activityId, unionId);
        return GtJsonResult.instanceSuccessMsg();
    }

}