package com.gt.union.card.main.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.service.IUnionCardApplyService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.main.vo.CardApplyVO;
import com.gt.union.card.main.vo.CardPhoneVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.EncryptUtil;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.QRcodeKit;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.vo.UnionPayVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
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

    @Autowired
    private IUnionCardService unionCardService;

    @Resource(name = "unionBackCardApplyService")
    private IUnionCardApplyService unionCardApplyService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "前台-办理联盟卡-查询联盟和联盟卡", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/fanId/{fanId}/apply", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getCardApplyVO(
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
        CardApplyVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(CardApplyVO.class);
            List<UnionMain> unionList = MockUtil.list(UnionMain.class, 3);
            result.setUnionList(unionList);
            List<UnionCardActivity> activityList = MockUtil.list(UnionCardActivity.class, 3);
            result.setActivityList(activityList);
        } else {
            result = unionCardService.getCardApplyVOByBusIdAndFanId(busId, fanId, unionId);
        }

        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "获取联盟卡手机端二维码", notes = "获取联盟卡手机端二维码", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/qr/h5Card", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public void qrH5Card(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        String url = ConfigConstant.CARD_PHONE_BASE_URL + "toUnionCard/" + busId;
        QRcodeKit.buildQRcode(url, 250, 250, response);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "前台-办理联盟卡-校验手机验证码", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/apply/phone", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String checkCardPhoneVO(
            HttpServletRequest request,
            @ApiParam(value = "表单内容", name = "cardPhoneVO", required = true)
            @RequestBody CardPhoneVO vo) throws Exception {
        // mock
        UnionCardFan result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(UnionCardFan.class);
        } else {
            result = unionCardService.checkCardPhoneVO(vo);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "前台-办理联盟卡-确定", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/fanId/{fanId}/unionId/{unionId}/apply", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveApplyByUnionId(
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
            busId = busUser.getPid();
        }
        // mock
        UnionPayVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(UnionPayVO.class);
        } else {
            result = unionCardService.saveApplyByBusIdAndUnionIdAndFanId(busId, unionId, fanId, activityIdList, unionCardApplyService);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

}