package com.gt.union.opportunity.brokerage.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PageUtil;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.opportunity.brokerage.vo.BrokerageOpportunityVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 佣金收入 前端控制器
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@Api(description = "佣金收入")
@RestController
@RequestMapping("/unionBrokerageIncome")
public class UnionBrokerageIncomeController {

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "佣金结算-我的佣金收入-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/opportunity/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<BrokerageOpportunityVO>> pageBrokerageOpportunityVO(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId,
            @ApiParam(value = "接收盟员id", name = "toMemberId")
            @RequestParam(value = "toMemberId", required = false) Integer toMemberId,
            @ApiParam(value = "是否已结算(0:否 1:是)", name = "isClose")
            @RequestParam(value = "isClose", required = false) Integer isClose,
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
//        List<BrokerageOpportunityVO> voList = MockUtil.list(BrokerageOpportunityVO.class, page.getSize());
        List<BrokerageOpportunityVO> voList = unionBrokerageIncomeService.listBrokerageOpportunityVOByBusId(busId, unionId, toMemberId, isClose, clientName, clientPhone);
        Page<BrokerageOpportunityVO> result = (Page<BrokerageOpportunityVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

}