package com.gt.union.brokerage.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.brokerage.service.IUnionBrokerageWithdrawalService;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.log.service.IUnionLogErrorService;
import com.gt.union.main.controller.IndexController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 佣金收入 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionBrokerageIncome")
public class UnionBrokerageIncomeController {
	private Logger logger = LoggerFactory.getLogger(UnionBrokerageIncomeController.class);

	@Autowired
	private IUnionLogErrorService unionLogErrorService;

	@Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

	@Autowired
	private IUnionBrokerageWithdrawalService unionBrokerageWithdrawalService;

	@ApiOperation(value = "分页获取售卡佣金分成列表信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/card/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String pageCardMapByMemberId(HttpServletRequest request, Page page
        , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                        @PathVariable("memberId") Integer memberId
        , @ApiParam(name = "cardType", value = "联盟卡类型，1为黑卡，2为红卡")
                                        @RequestParam(value = "cardType", required = false) Integer cardType
        , @ApiParam(name = "cardNumber", value = "联盟卡号，模糊匹配")
                                        @RequestParam(value = "cardNumber", required = false) String cardNumber) {
	    try {
	        BusUser busUser = SessionUtils.getLoginUser(request);
	        Integer busId = busUser.getId();
	        if (busUser.getPid() != null && busUser.getPid() == BusUserConstant.ACCOUNT_TYPE_UNVALID) {
	            busId = busUser.getPid();
	        }
	        Page result = this.unionBrokerageIncomeService.pageCardMapByBusIdAndMemberId(page, busId, memberId, cardType, cardNumber);
	        return GTJsonResult.instanceSuccessMsg(result).toString();
	    } catch (BaseException e) {
	        logger.error("", e);
	        this.unionLogErrorService.saveIfNotNull(e);
	        return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
	    } catch (Exception e) {
	        logger.error("", e);
	        this.unionLogErrorService.saveIfNotNull(e);
	        return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
	    }
	}




	/************************************************佣金平台************************************************************************/
	@ApiOperation(value = "佣金平台可提现总额", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/withdrawalSum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String withdrawalSum(HttpServletRequest request) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Integer busId = busUser.getId();
			if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
				busId = busUser.getPid();
			}
			double sumPay = unionBrokerageIncomeService.getSumInComeUnionBrokerage(busId); //收入的佣金总和
			double sumWithdrawals = unionBrokerageWithdrawalService.getSumWithdrawalsUnionBrokerage(busId);//已提现的佣金总和
			double ableGet = BigDecimalUtil.subtract(sumPay,sumWithdrawals).doubleValue();//可提现
			return GTJsonResult.instanceSuccessMsg(ableGet).toString();
		} catch (Exception e) {
			logger.error("", e);
			this.unionLogErrorService.saveIfNotNull(e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}




}
