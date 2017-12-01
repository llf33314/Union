package com.gt.union.opportunity.brokerage.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PageUtil;
import com.gt.union.opportunity.brokerage.vo.BrokerageOpportunityVO;
import com.gt.union.opportunity.brokerage.vo.BrokeragePayVO;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 佣金支出 前端控制器
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@Api(description = "佣金支出")
@RestController
@RequestMapping("/unionBrokeragePay")
public class UnionBrokeragePayController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页：获取我需支付的商机佣金信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/opportunity/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<BrokerageOpportunityVO>> pageBrokerageOpportunityVO(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId,
            @ApiParam(value = "推荐商家盟员id", name = "toMemberId")
            @RequestParam(value = "toMemberId", required = false) Integer toMemberId,
            @ApiParam(value = "是否已结算(0:否 1:是)", name = "isClose")
            @RequestParam(value = "isClose", required = false) String isClose,
            @ApiParam(value = "客户名称", name = "clientName")
            @RequestParam(value = "clientName", required = false) String clientName,
            @ApiParam(value = "客户电话", name = "clientPhone")
            @RequestParam(value = "clientPhone", required = false) String clientPhone) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<BrokerageOpportunityVO> voList = MockUtil.list(BrokerageOpportunityVO.class, page.getSize());
        Page<BrokerageOpportunityVO> result = (Page<BrokerageOpportunityVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "分页：获取商机佣金支付明细信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/detail/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<BrokeragePayVO>> pagePayVo(
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
        List<BrokeragePayVO> voList = MockUtil.list(BrokeragePayVO.class, page.getSize());
        for (int i = 0; i < voList.size(); i++) {
            List<UnionOpportunity> opportunityList = MockUtil.list(UnionOpportunity.class, 20);
            voList.get(i).setOpportunityList(opportunityList);
        }
        Page<BrokeragePayVO> result = (Page<BrokeragePayVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "导出：商机佣金支付明细信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/detail/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> exportBrokeragePayDetail(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg("TODO");
    }

    @ApiOperation(value = "获取商机佣金支付明细详情信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/detail", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<BrokeragePayVO> getPayVo(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "盟员id", name = "memberId", required = true)
            @RequestParam(value = "memberId") Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        BrokeragePayVO result = MockUtil.get(BrokeragePayVO.class);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "导出：商机佣金支付明细详情信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/detail/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> exportDetail(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "盟员id", name = "memberId", required = true)
            @RequestParam(value = "memberId") Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg("TODO");
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    //------------------------------------------------- patch ----------------------------------------------------------

    @ApiOperation(value = "批量支付：商机佣金", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/opportunity", method = RequestMethod.PATCH, produces = "application/json;charset=UTF-8")
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

    @ApiOperation(value = "批量支付：商机佣金-回调", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/opportunity/callback", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
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

}