package com.gt.union.controller.brokerage;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.brokerage.IUnionBrokerageWithdrawalsRecordService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 联盟佣金提现记录 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@RestController
@RequestMapping("/unionBrokerageWithdrawalsRecord")
public class UnionBrokerageWithdrawalsRecordController {
    private static final String PAGE_MAP_BUSID = "UnionBrokerageWithdrawalsRecordController.pageMapByBusId()";
    private static final String PAGE_MAP_UNIONID_BUSID = "UnionBrokerageWithdrawalsRecordController.pageMapByUnionIdAndBusId()";
    private static final String SUM_MONEY_BUSID = "UnionBrokerageWithdrawalsRecordController.sumMoneyByBusId()";

    private Logger logger = LoggerFactory.getLogger(UnionBrokerageWithdrawalsRecordController.class);

    @Autowired
    private IUnionBrokerageWithdrawalsRecordService unionBrokerageWithdrawalsRecordService;

    @ApiOperation(value = "查询我的提现记录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String pageMapByBusId(Page page, HttpServletRequest request) {
	    try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Page result = this.unionBrokerageWithdrawalsRecordService.pageMapByBusId(page, busUser.getId());
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
	        logger.error("", e);
	        return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
	        logger.error("", e);
	        return GTJsonResult.instanceErrorMsg(PAGE_MAP_BUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "查询我在某联盟的提现记录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageMapByUnionIdAndBusId(Page page, HttpServletRequest request
        , @ApiParam(value = "联盟id", name = "unionId", required = true) @PathVariable("unionId") Integer unionId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Page result = this.unionBrokerageWithdrawalsRecordService.pageMapByUnionIdAndBusId(page, unionId, busUser.getId());
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(PAGE_MAP_UNIONID_BUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "统计我的历史提现金额总和", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumMoneyByBusId(HttpServletRequest request) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Double moneySum = this.unionBrokerageWithdrawalsRecordService.sumMoneyByBusId(busUser.getId());
            return GTJsonResult.instanceSuccessMsg(moneySum).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(SUM_MONEY_BUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "统计我在某联盟的历史提现金额总和", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumMoneyByUnionIdAndBusId(HttpServletRequest request
        , @ApiParam(value = "联盟id", name = "unionId", required = true) @PathVariable("unionId") Integer unionId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Double moneySum = this.unionBrokerageWithdrawalsRecordService.sumMoneyByUnionIdAndBusId(unionId, busUser.getId());
            return GTJsonResult.instanceSuccessMsg(moneySum).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(SUM_MONEY_BUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }
}
