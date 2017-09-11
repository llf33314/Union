package com.gt.union.brokerage.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.brokerage.entity.UnionIncomeExpense;

/**
 * <p>
 * 商家收支表 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionIncomeExpenseService extends IService<UnionIncomeExpense> {
    /**
     * 根据联盟id、商家id和收支类型，统计金额
     *
     * @param unionId {not null} 联盟id
     * @param busId   {not null} 商家id
     * @param type    {not null} 收支类型
     * @return
     * @throws Exception
     */
    Double sumMoneyByUnionIdAndBusIdAndType(Integer unionId, Integer busId, Integer type) throws Exception;

    /**
     * 根据联盟id和商家id，获取商家在联盟的净利额，即收入-支出
     *
     * @param unionId {not null} 联盟id
     * @param busId   {not null} 商家id
     * @return
     * @throws Exception
     */
    Double getMoneyProfitByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception;
}
