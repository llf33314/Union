package com.gt.union.card.sharing.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.sharing.service.IUnionCardSharingRatioService;
import com.gt.union.card.sharing.vo.CardSharingRatioVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
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
 * 联盟卡售卡分成比例 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Api(description = "联盟卡售卡分成比例")
@RestController
@RequestMapping("/unionCardSharingRatio")
public class UnionCardSharingRatioController {

    @Autowired
    private IUnionCardSharingRatioService unionCardSharingRatioService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "售卡佣金分成管理-活动卡售卡比例设置-分页-查看比例", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<CardSharingRatioVO>> pageSharingRatioVOByActivityIdAndUnionId(
            HttpServletRequest request,
            Page page,
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
        List<CardSharingRatioVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(CardSharingRatioVO.class, page.getSize());
        } else {
            voList = unionCardSharingRatioService.listCardSharingRatioVOByBusIdAndUnionIdAndActivityId(busId, unionId, activityId);
        }
        Page<CardSharingRatioVO> result = (Page<CardSharingRatioVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "售卡佣金分成管理-活动卡售卡比例设置-分页-查看比例-比例设置", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<List<CardSharingRatioVO>> listByActivityIdAndUnionId(
            HttpServletRequest request,
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
        List<CardSharingRatioVO> result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.list(CardSharingRatioVO.class, 20);
        } else {
            result = unionCardSharingRatioService.listCardSharingRatioVOByBusIdAndUnionIdAndActivityId(busId, unionId, activityId);
        }
        return GtJsonResult.instanceSuccessMsg(result);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "售卡佣金分成管理-活动卡售卡比例设置-分页-查看比例-比例设置-更新", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> updateRatioByActivityIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟卡活动id", name = "activityId", required = true)
            @PathVariable("activityId") Integer activityId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "表单信息", name = "ratioList", required = true)
            @RequestBody List<CardSharingRatioVO> ratioList) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionCardSharingRatioService.updateRatioByBusIdAndUnionIdAndActivityId(busId, unionId, activityId, ratioList);
        }
        return GtJsonResult.instanceSuccessMsg();
    }

    //-------------------------------------------------- post ----------------------------------------------------------


}