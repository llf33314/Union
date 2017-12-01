package com.gt.union.h5.brokerage.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.h5.brokerage.vo.*;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageWithdrawal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * H5佣金平台 前端管理器
 *
 * @author linweicong
 * @version 2017-12-01 11:27:08
 */
@Api(description = "H5佣金平台")
@RestController
@RequestMapping("/h5Brokerage")
public class H5BrokerageController {

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "首页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "index", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<IndexVO> getIndexVO(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        IndexVO result = MockUtil.get(IndexVO.class);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "我要提现", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<WithdrawalVO> getWithdrawalVO(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        WithdrawalVO result = MockUtil.get(WithdrawalVO.class);
        List<UnionBrokerageWithdrawal> withdrawalList = MockUtil.list(UnionBrokerageWithdrawal.class, 20);
        result.setWithdrawalList(withdrawalList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "我要提现-佣金明细-推荐佣金", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/opportunity", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<OpportunityBrokerageDetailVO> getOpportunityBrokerageDetailVO(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        OpportunityBrokerageDetailVO result = MockUtil.get(OpportunityBrokerageDetailVO.class);
        List<OpportunityBrokerage> paidOpportunityBrokerageList = MockUtil.list(OpportunityBrokerage.class, 20);
        result.setPaidOpportunityBrokerageList(paidOpportunityBrokerageList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "我要提现-佣金明细-售卡佣金", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/card", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<CardBrokerageDetailVO> getCardBrokerageDetailVO(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        CardBrokerageDetailVO result = MockUtil.get(CardBrokerageDetailVO.class);
        List<CardBrokerage> cardBrokerageList = MockUtil.list(CardBrokerage.class, 20);
        result.setCardBrokerageList(cardBrokerageList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "我需支付-未支付", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/unPaid", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<UnPaidOpportunityBrokerageVO> getUnPaidOpportunityBrokerageVO(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        UnPaidOpportunityBrokerageVO result = MockUtil.get(UnPaidOpportunityBrokerageVO.class);
        List<OpportunityBrokerage> unPaidOpportunityBrokerageList = MockUtil.list(OpportunityBrokerage.class, 20);
        result.setUnPaidOpportunityBrokerageList(unPaidOpportunityBrokerageList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "我需支付-已支付", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/paid", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<PaidOpportunityBrokerageVO> getPaidOpportunityBrokerageVO(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        PaidOpportunityBrokerageVO result = MockUtil.get(PaidOpportunityBrokerageVO.class);
        List<OpportunityBrokerage> paidOpportunityBrokerageList = MockUtil.list(OpportunityBrokerage.class, 20);
        result.setPaidOpportunityBrokerageList(paidOpportunityBrokerageList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "我未收佣金", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unreceived", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<UnReceivedOpportunityBrokerageVO> getUnReceivedOpportunityBrokerageVO(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        UnReceivedOpportunityBrokerageVO result = MockUtil.get(UnReceivedOpportunityBrokerageVO.class);
        List<OpportunityBrokerage> unReceivedOpportunityBrokerageList = MockUtil.list(OpportunityBrokerage.class, 20);
        result.setUnReceivedOpportunityBrokerageList(unReceivedOpportunityBrokerageList);
        return GtJsonResult.instanceSuccessMsg(result);
    }


    //-------------------------------------------------- put ----------------------------------------------------------
    //------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "账号密码登录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/login/userPassword", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> loginByUserPassword(
            HttpServletRequest request,
            @ApiParam(value = "表单内容", name = "loginUserPassword", required = true)
            @RequestBody LoginUserPassword loginUserPassword) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }

    @ApiOperation(value = "手机号登录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/login/phone", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> loginByPhone(
            HttpServletRequest request,
            @ApiParam(value = "表单内容", name = "loginPhone", required = true)
            @RequestBody LoginPhone loginPhone) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }

    @ApiOperation(value = "我要提现-立即提现", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> withdrawal(
            HttpServletRequest request,
            @ApiParam(value = "提现金额", name = "money", required = true)
            @RequestBody Double money) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }

    @ApiOperation(value = "我未收佣金-催促", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unreceived/urge", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> urgeUnreceived(
            HttpServletRequest request,
            @ApiParam(value = "商机id", name = "opportunityId", required = true)
            @RequestParam(value = "opportunityId") Integer opportunityId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }
}
