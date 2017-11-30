package com.gt.union.card.main.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.main.vo.CardApplyPayVO;
import com.gt.union.card.main.vo.CardApplyVO;
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
 * 联盟卡 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Api(description = "联盟卡")
@RestController
@RequestMapping("/unionCard")
public class UnionCardController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "获取办理联盟卡中的联盟和活动卡信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/apply", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<List<CardApplyVO>> getCardApplyVO(
            HttpServletRequest request,
            @ApiParam(value = "手机号", name = "phone", required = true)
            @RequestParam(value = "phone") String phone,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<CardApplyVO> result = MockUtil.list(CardApplyVO.class, 3);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "购买联盟卡", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/apply", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<CardApplyPayVO> saveApplyByUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "手机号", name = "phone", required = true)
            @RequestParam(value = "phone") String phone,
            @ApiParam(value = "活动id列表", name = "activityIdList", required = true)
            @RequestBody List<Integer> activityIdList) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }

        // mock
        CardApplyPayVO result = MockUtil.get(CardApplyPayVO.class);
        return GtJsonResult.instanceSuccessMsg(result);
    }

}