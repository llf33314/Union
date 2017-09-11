package com.gt.union.brokerage.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.brokerage.constant.BrokerageConstant;
import com.gt.union.brokerage.entity.UnionIncomeExpense;
import com.gt.union.brokerage.mapper.UnionIncomeExpenseMapper;
import com.gt.union.brokerage.service.IUnionIncomeExpenseService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 商家收支表 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionIncomeExpenseServiceImpl extends ServiceImpl<UnionIncomeExpenseMapper, UnionIncomeExpense> implements IUnionIncomeExpenseService {
    /**
     * 根据联盟id、商家id和收支类型，统计金额
     *
     * @param unionId {not null} 联盟id
     * @param busId   {not null} 商家id
     * @param type    {not null} 收支类型
     * @return
     * @throws Exception
     */
    @Override
    public Double sumMoneyByUnionIdAndBusIdAndType(final Integer unionId, final Integer busId, final Integer type) throws Exception {
        if (unionId == null || busId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" ie")
                        .append(" WHERE ie.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND ie.union_id = ").append(unionId)
                        .append("  AND ie.bus_id = ").append(busId)
                        .append("  AND ie.type = ").append(type);
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" IFNULL(SUM(ie.money), 0) moneySum");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        Map<String, Object> sqlResultMap = this.selectMap(wrapper);
        Object objMoneySum = sqlResultMap.get("moneySum");
        return Double.valueOf(objMoneySum != null ? objMoneySum.toString() : "0");
    }

    /**
     * 根据联盟id和商家id，获取商家在联盟的净利额，即收入-支出
     *
     * @param unionId {not null} 联盟id
     * @param busId   {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public Double getMoneyProfitByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception {
        if (unionId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Double incomeMoneySum = this.sumMoneyByUnionIdAndBusIdAndType(unionId, busId, BrokerageConstant.TYPE_INCOME);
        Double expenseMoneySum = this.sumMoneyByUnionIdAndBusIdAndType(unionId, busId, BrokerageConstant.TYPE_EXPENSE);

        return BigDecimalUtil.subtract(incomeMoneySum, expenseMoneySum).doubleValue();
    }
}
