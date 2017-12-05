package com.gt.union.h5.brokerage.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PageUtil;
import com.gt.union.h5.brokerage.vo.*;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.union.main.entity.UnionMain;
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

    @ApiOperation(value = "获取我的联盟列表", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/myUnion", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<List<UnionMain>> listMyUnion(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionMain> result = MockUtil.list(UnionMain.class, 3);
        return GtJsonResult.instanceSuccessMsg(result);
    }

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
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "分页：我要提现-提现记录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/history/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<UnionBrokerageWithdrawal>> getWithdrawalHistory(
            HttpServletRequest request,
            Page page) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionBrokerageWithdrawal> withdrawalList = MockUtil.list(UnionBrokerageWithdrawal.class, page.getSize());
        Page<UnionBrokerageWithdrawal> result = (Page<UnionBrokerageWithdrawal>) page;
        result = PageUtil.setRecord(result, withdrawalList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "我要提现-佣金明细-推荐佣金-已支付的商机佣金总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/opportunity/paidSum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Double> getOpportunityBrokeragePaidSum(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        Double result = MockUtil.get(Double.class);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "分页：我要提现-佣金明细-推荐佣金-明细", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/opportunity/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<OpportunityBrokerageVO>> pageOpportunityBrokerageVO(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<OpportunityBrokerageVO> voList = MockUtil.list(OpportunityBrokerageVO.class, page.getSize());
        Page<OpportunityBrokerageVO> result = (Page<OpportunityBrokerageVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "我要提现-佣金明细-售卡佣金-已支付的售卡佣金总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/card/paidSum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Double> getCardBrokeragePaidSum(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        Double result = MockUtil.get(Double.class);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "分页：我要提现-佣金明细-售卡佣金-明细", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/card/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<CardBrokerageVO>> pageCardBrokerageVO(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<CardBrokerageVO> voList = MockUtil.list(CardBrokerageVO.class, page.getSize());
        Page<CardBrokerageVO> result = (Page<CardBrokerageVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "我需支付-未支付-佣金总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/unPaid/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Double> getUnPaidOpportunityBrokerageSum(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        Double result = MockUtil.get(Double.class);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "分页：我需支付-未支付-明细", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/unPaid/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<OpportunityBrokerageVO>> pageUnPaidOpportunityBrokerageVO(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<OpportunityBrokerageVO> voList = MockUtil.list(OpportunityBrokerageVO.class, page.getSize());
        Page<OpportunityBrokerageVO> result = (Page<OpportunityBrokerageVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "我需支付-已支付-佣金总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/paid/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Double> getPaidOpportunityBrokerageSum(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        Double result = MockUtil.get(Double.class);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "分页：我需支付-已支付-明细", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/paid/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<OpportunityBrokerageVO>> pagePaidOpportunityBrokerageVO(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<OpportunityBrokerageVO> voList = MockUtil.list(OpportunityBrokerageVO.class, page.getSize());
        Page<OpportunityBrokerageVO> result = (Page<OpportunityBrokerageVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "我未收佣金-佣金总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unreceived/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Double> getUnReceivedOpportunityBrokerageSum(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        Double result = MockUtil.get(Double.class);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "分页：我未收佣金-明细", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unreceived/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<OpportunityBrokerageVO>> pageUnReceivedOpportunityBrokerageVO(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<OpportunityBrokerageVO> voList = MockUtil.list(OpportunityBrokerageVO.class, page.getSize());
        Page<OpportunityBrokerageVO> result = (Page<OpportunityBrokerageVO>) page;
        result = PageUtil.setRecord(result, voList);
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

    @ApiOperation(value = "我需支付-未支付-去支付和一键支付-回调", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/79B4DE7C/pay/unpaid/callback", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult updateCallback(
            HttpServletRequest request,
            @ApiParam(value = "商机id列表", name = "opportunityIdList", required = true)
            @RequestParam(value = "opportunityIdList") String opportunityIdList) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }

    //------------------------------------------------- patch ----------------------------------------------------------

    @ApiOperation(value = "我需支付-未支付-去支付和一键支付", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/unpaid", method = RequestMethod.PATCH, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> batchPay(
            HttpServletRequest request,
            @ApiParam(value = "商机id列表", name = "opportunityIdList", required = true)
            @RequestParam(value = "opportunityIdList") List<Integer> opportunityIdList) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }
}
