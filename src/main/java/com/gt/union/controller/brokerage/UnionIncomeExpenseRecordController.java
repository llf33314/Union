package com.gt.union.controller.brokerage;

import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.brokerage.IUnionIncomeExpenseRecordService;
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
 * 商家收支记录表 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-08-28
 */
@RestController
@RequestMapping("/unionIncomeExpenseRecord")
public class UnionIncomeExpenseRecordController {
    private static final String SUM_MONEY_BUSID_TYPE = "UnionIncomeExpenseRecordController.sumMoneyByBusIdAndType()";
    private static final String SUM_MONEY_UNIONID_BUSID_TYPE = "UnionIncomeExpenseRecordController.sumMoneyByUnionIdAndBusIdAndType()";
    private static final String GET_PROFIT_MONEY_BUSID = "UnionIncomeExpenseRecordController.getProfitMoneyByBusId()";
    private static final String GET_PROFIT_MONEY_UNIONID_BUSID = "UnionIncomeExpenseRecordController.getProfitMoneyByUnionIdAndBusId()";

    private Logger logger = LoggerFactory.getLogger(UnionIncomeExpenseRecordController.class);

    @Autowired
    private IUnionIncomeExpenseRecordService unionIncomeExpenseRecordService;

    @ApiOperation(value = "获取佣金总收入或总支出", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/type/{type}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumMoneyByBusIdAndType(HttpServletRequest request
        , @ApiParam(name = "type", value = "收支类型：1为收入，2为支出", required = true) @PathVariable("type") Integer type) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Double moneySum = this.unionIncomeExpenseRecordService.sumMoneyByBusIdAndType(busUser.getId(), type);
            return GTJsonResult.instanceSuccessMsg(moneySum).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(SUM_MONEY_BUSID_TYPE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "获取在某联盟的佣金总收入或总支出", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/type/{type}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumMoneyByUnionIdAndBusIdAndType(HttpServletRequest request
            , @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId
            , @ApiParam(name = "type", value = "收支类型：1为收入，2为支出", required = true) @PathVariable("type") Integer type) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Double moneySum = this.unionIncomeExpenseRecordService.sumMoneyByUnionIdAndBusIdAndType(unionId, busUser.getId(), type);
            return GTJsonResult.instanceSuccessMsg(moneySum).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(SUM_MONEY_UNIONID_BUSID_TYPE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "获取商家的收支盈利金额，即可提现金额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/profit", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getProfitMoneyByBusId(HttpServletRequest request) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Double moneySum = this.unionIncomeExpenseRecordService.getProfitMoneyByBusId(busUser.getId());
            return GTJsonResult.instanceSuccessMsg(moneySum).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(GET_PROFIT_MONEY_BUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "获取商家在某联盟的收支盈利金额，即可提现金额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/profit", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getProfitMoneyByUnionIdAndBusId(HttpServletRequest request
            , @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Double moneySum = this.unionIncomeExpenseRecordService.getProfitMoneyByUnionIdAndBusId(unionId, busUser.getId());
            return GTJsonResult.instanceSuccessMsg(moneySum).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(GET_PROFIT_MONEY_UNIONID_BUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }
}
