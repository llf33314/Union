package com.gt.union.service.brokerage.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.brokerage.UnionIncomeExpenseRecordConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.entity.brokerage.UnionIncomeExpenseRecord;
import com.gt.union.mapper.brokerage.UnionIncomeExpenseRecordMapper;
import com.gt.union.service.brokerage.IUnionIncomeExpenseRecordService;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * <p>
 * 商家收支记录表 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-08-28
 */
@Service
public class UnionIncomeExpenseRecordServiceImpl extends ServiceImpl<UnionIncomeExpenseRecordMapper, UnionIncomeExpenseRecord> implements IUnionIncomeExpenseRecordService {
    private static final String SUM_MONEY_BUSID_TYPE = "UnionIncomeExpenseRecordServiceImpl.sumMoneyByBusIdAndType()";
    private static final String GET_PROFIT_MONEY_BUSID = "UnionIncomeExpenseRecordServiceImpl.getProfitMoneyByBusId()";
    private static final String SUM_MONEY_UNIONID_BUSID_TYPE = "UnionIncomeExpenseRecordServiceImpl.sumMoneyByUnionIdAndBusIdAndType()";
    private static final String GET_PROFIT_MONEY_UNIONID_BUSID = "UnionIncomeExpenseRecordServiceImpl.getProfitMoneyByUnionIdAndBusId()";

    @Override
    public Double sumMoneyByBusIdAndType(final Integer busId, final Integer type) throws Exception {
        if (busId == null) {
            throw new ParamException(SUM_MONEY_BUSID_TYPE, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (type == null) {
            throw new ParamException(SUM_MONEY_BUSID_TYPE, "参数type为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                return new StringBuilder(" r")
                        .append(" WHERE r.del_status = ").append(UnionIncomeExpenseRecordConstant.DEL_STATUS_NO)
                        .append("  AND r.bus_id = ").append(busId)
                        .append("  AND r.type = ").append(type)
                        .toString();
            }
        };
        wrapper.setSqlSelect(" SUM(IFNULL(r.money, 0)) moneySum");
        Map<String, Object> map = this.selectMap(wrapper);

        return map != null && map.get("moneySum") != null ? Double.valueOf(map.get("moneySum").toString()) : null;
    }

    @Override
    public Double getProfitMoneyByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(GET_PROFIT_MONEY_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        Double incomeMoney = this.sumMoneyByBusIdAndType(busId, UnionIncomeExpenseRecordConstant.TYPE_INCOME);
        Double expenseMoney = this.sumMoneyByBusIdAndType(busId, UnionIncomeExpenseRecordConstant.TYPE_EXPENSE);
        return BigDecimalUtil.subtract(incomeMoney, expenseMoney).doubleValue();
    }

    @Override
    public Double sumMoneyByUnionIdAndBusIdAndType(final Integer unionId, final Integer busId,final Integer type) throws Exception {
        if (unionId == null) {
            throw new ParamException(SUM_MONEY_UNIONID_BUSID_TYPE, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(SUM_MONEY_UNIONID_BUSID_TYPE, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (type == null) {
            throw new ParamException(SUM_MONEY_UNIONID_BUSID_TYPE, "参数type为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                return new StringBuilder(" r")
                        .append(" WHERE r.del_status = ").append(UnionIncomeExpenseRecordConstant.DEL_STATUS_NO)
                        .append("  AND r.union_id = ").append(unionId)
                        .append("  AND r.bus_id = ").append(busId)
                        .append("  AND r.type = ").append(type)
                        .toString();
            }
        };
        wrapper.setSqlSelect(" SUM(IFNULL(r.money, 0)) moneySum");
        Map<String, Object> map = this.selectMap(wrapper);

        return map != null && map.get("moneySum") != null ? Double.valueOf(map.get("moneySum").toString()) : null;
    }

    @Override
    public Double getProfitMoneyByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception {
        if (unionId == null) {
            throw new ParamException(GET_PROFIT_MONEY_UNIONID_BUSID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(GET_PROFIT_MONEY_UNIONID_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        Double incomeMoney = this.getProfitMoneyByUnionIdAndBusId(unionId, busId);
        Double expenseMoney = this.getProfitMoneyByUnionIdAndBusId(unionId, busId);
        return BigDecimalUtil.subtract(incomeMoney, expenseMoney).doubleValue();
    }
}
