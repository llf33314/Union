package com.gt.union.card.main.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.card.main.service.IUnionCardApplyService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.main.vo.*;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.union.main.vo.UnionPayVO;
import com.gt.util.entity.result.wx.ApiWxApplet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Date;
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

    @Autowired
    private IBusUserService busUserService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "前台-办理联盟卡-联盟卡列表", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/apply", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getCardApplyVO(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        CardApplyVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(CardApplyVO.class);
        } else {
            result = unionCardService.getCardApplyVOByBusId(busId);
        }

        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "财务-数据统计-联盟折扣卡领卡统计", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/discountCard/statistics", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getDiscountCardStatisticsVO(
            HttpServletRequest request,
            @ApiParam(value = "1:按天统计;2:按月统计", name = "type", required = true)
            @RequestParam(value = "type") Integer type,
            @ApiParam(value = "统计开始时间", name = "beginTime", required = true)
            @RequestParam(value = "beginTime") Long beginTime,
            @ApiParam(value = "统计结束时间", name = "endTime")
            @RequestParam(value = "endTime", required = false) Long endTime) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<DiscountCardStatisticsVO> result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.list(DiscountCardStatisticsVO.class, 3);
        } else {
            Date begin = beginTime != null ? (new Date(beginTime)) : null;
            Date end = endTime != null ? (new Date(endTime)) : DateUtil.getCurrentDate();
            end = DateUtil.parseDate(DateUtil.getDateString(end, DateUtil.DATE_PATTERN) + " 23:59:59", DateUtil.DATETIME_PATTERN);
            result = unionCardService.listDiscountCardStatisticsVOByBusId(busId, type, begin, end);
        }

        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "财务-数据统计-联盟活动卡发售统计", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityCard/statistics", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getActivityCardStatisticsVO(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<ActivityCardStatisticsVO> result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.list(ActivityCardStatisticsVO.class, 3);
        } else {
            result = unionCardService.listActivityCardStatisticsVOByBusId(busId);
        }

        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "获取联盟卡粉丝端二维码", notes = "获取联盟卡粉丝端二维码", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/qr/applet", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public String qrH5Card(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        ApiWxApplet wxApplet = busUserService.getBusIdAndIndustry(busId, PropertiesUtil.getAppIndustry());
        if (wxApplet != null) {
            String imgUrl = PropertiesUtil.getResourceUrl() + wxApplet.getExperienceQrUrl();
            return GtJsonResult.instanceSuccessMsg(imgUrl).toString();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
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
        CardPhoneResponseVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(CardPhoneResponseVO.class);
        } else {
            result = unionCardService.checkCardPhoneVO(vo);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "前台-办理联盟卡-确定", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/apply", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveApplyByUnionId(
            HttpServletRequest request,
            @ApiParam(value = "办理联盟卡对象", name = "applyPostVO", required = true)
            @RequestBody CardPhoneResponseVO applyPostVO) throws Exception {
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
            result = unionCardService.saveApplyByBusId(busId, applyPostVO, unionCardApplyService);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

}