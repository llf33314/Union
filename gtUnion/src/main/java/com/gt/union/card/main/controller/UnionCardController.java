package com.gt.union.card.main.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.main.vo.CardApplyVO;
import com.gt.union.card.main.vo.CardSocketVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.union.main.entity.UnionMain;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private IUnionCardService unionCardService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "前台-办理联盟卡-查询联盟和联盟卡", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/fanId/{fanId}/apply", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<CardApplyVO> getCardApplyVO(
            HttpServletRequest request,
            @ApiParam(value = "粉丝id", name = "fanId", required = true)
            @PathVariable("fanId") Integer fanId,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        CardApplyVO result = MockUtil.get(CardApplyVO.class);
        List<UnionMain> unionList = MockUtil.list(UnionMain.class, 3);
        result.setUnionList(unionList);
        List<UnionCardActivity> activityList = MockUtil.list(UnionCardActivity.class, 3);
        result.setActivityList(activityList);

//        CardApplyVO result = unionCardService.getCardApplyVOByBusIdAndFanId(busId, fanId, unionId);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "购买联盟卡", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/fanId/{fanId}/unionId/{unionId}/apply", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<CardSocketVO> saveApplyByUnionId(
            HttpServletRequest request,
            @ApiParam(value = "粉丝id", name = "fanId", required = true)
            @PathVariable("fanId") Integer fanId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "活动id列表", name = "activityIdList", required = true)
            @RequestBody List<Integer> activityIdList) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }

        // mock
        CardSocketVO result = MockUtil.get(CardSocketVO.class);
//        CardSocketVO result = unionCardService.saveApplyByBusIdAndFanIdAndUnionId(busId, fanId, unionId, activityIdList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "购买联盟卡-回调", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/79B4DE7C/callback", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult method(
            @ApiParam(value = "联盟许可id", name = "permitId", required = true)
            @PathVariable("permitId") Integer permitId,
            @ApiParam(value = "socket关键字", name = "socketKey", required = true)
            @RequestParam(value = "socketKey") String socketKey,
            @RequestBody Map<String, Object> param) throws Exception {
        return GtJsonResult.instanceSuccessMsg();
    }

}