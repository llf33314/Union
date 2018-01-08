package com.gt.union.opportunity.brokerage.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageWithdrawalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 佣金提现 前端控制器
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@Api(description = "佣金提现")
@RestController
@RequestMapping("/unionBrokerageWithdrawal")
public class UnionBrokerageWithdrawalController {

    @Autowired
    private IUnionBrokerageWithdrawalService unionBrokerageWithdrawalService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "获取可提现金额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/available", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getAvailableMoney(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        Double result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(Double.class);
        } else {
            result = unionBrokerageWithdrawalService.getValidAvailableMoneyByBusId(busId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

}