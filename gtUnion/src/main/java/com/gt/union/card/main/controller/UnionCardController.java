package com.gt.union.card.main.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.main.vo.CardApplyVO;
import com.gt.union.card.main.vo.CardPhoneVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.vo.UnionPayVO;
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

        CardApplyVO result2 = unionCardService.getCardApplyVOByBusIdAndFanId(busId, fanId, unionId);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "前台-办理联盟卡-校验手机验证码", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/apply/phone", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<UnionCardFan> checkCardPhoneVO(
            HttpServletRequest request,
            @ApiParam(value = "表单内容", name = "cardPhoneVO", required = true)
            @RequestBody CardPhoneVO vo) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        UnionCardFan result = MockUtil.get(UnionCardFan.class);
        UnionCardFan result2 = unionCardService.checkCardPhoneVO(vo);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "前台-办理联盟卡-确定", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/fanId/{fanId}/unionId/{unionId}/apply", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<UnionPayVO> saveApplyByUnionId(
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
        UnionPayVO result = MockUtil.get(UnionPayVO.class);
        UnionPayVO result2 = unionCardService.saveApplyByBusIdAndUnionIdAndFanId(busId, unionId, fanId, activityIdList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

}