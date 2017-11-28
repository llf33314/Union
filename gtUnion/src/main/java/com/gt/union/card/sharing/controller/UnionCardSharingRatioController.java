package com.gt.union.card.sharing.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.sharing.vo.CardSharingRatioVO;
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
 * 联盟卡售卡分成比例 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Api(description = "联盟卡售卡分成比例")
@RestController
@RequestMapping("/unionCardSharingRatio")
public class UnionCardSharingRatioController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页获取活动卡售卡分成比例", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageSharingRatioVOByActivityIdAndUnionId(HttpServletRequest request,
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
        List<CardSharingRatioVO> voList = MockUtil.list(CardSharingRatioVO.class, page.getSize());
        page.setRecords(voList);
        return GtJsonResult.instanceSuccessMsg(page).toString();
    }

    @ApiOperation(value = "获取售卡分成比例", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listByActivityIdAndUnionId(HttpServletRequest request,
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
        List<CardSharingRatioVO> result = MockUtil.list(CardSharingRatioVO.class, 20);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------
    
    
    //------------------------------------------------- patch ----------------------------------------------------------

    @ApiOperation(value = "批量更新售卡分成比例", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}", method = RequestMethod.PATCH, produces = "application/json;charset=UTF-8")
    public String updateByActivityIdAndUnionId(HttpServletRequest request,
                                               @ApiParam(value = "联盟卡活动id", name = "activityId", required = true)
                                               @PathVariable("activityId") Integer activityId,
                                               @ApiParam(value = "联盟id", name = "unionId", required = true)
                                               @PathVariable("unionId") Integer unionId,
                                               @ApiParam(value = "表单信息", name = "ratioList", required = true)
                                               @RequestBody List<CardSharingRatioVO> ratioList) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }
    
}