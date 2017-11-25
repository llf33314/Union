package com.gt.union.card.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.vo.SharingRatioVO;
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

    @ApiOperation(value = "根据联盟卡活动id和联盟id，分页获取售卡分成比例", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageByActivityIdAndUnionId(HttpServletRequest request,
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
        List<SharingRatioVO> voList = MockUtil.list(SharingRatioVO.class, page.getSize());
        page.setRecords(voList);
        return GtJsonResult.instanceSuccessMsg(page).toString();
    }

    @ApiOperation(value = "根据联盟卡活动id和联盟id，获取售卡分成比例", produces = "application/json;charset=UTF-8")
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
        List<SharingRatioVO> result = MockUtil.list(SharingRatioVO.class, 20);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "根据联盟卡活动id、联盟id和表单信息，更新售卡分成比例", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateByActivityIdAndUnionId(HttpServletRequest request,
                                               @ApiParam(value = "联盟卡活动id", name = "activityId", required = true)
                                               @PathVariable("activityId") Integer activityId,
                                               @ApiParam(value = "联盟id", name = "unionId", required = true)
                                               @PathVariable("unionId") Integer unionId,
                                               @ApiParam(value = "表单信息", name = "ratioList", required = true)
                                               @RequestBody List<SharingRatioVO> ratioList) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

}