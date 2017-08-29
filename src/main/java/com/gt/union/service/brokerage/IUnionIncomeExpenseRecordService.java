package com.gt.union.service.brokerage;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.entity.brokerage.UnionIncomeExpenseRecord;

/**
 * <p>
 * 商家收支记录表 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-08-28
 */
public interface IUnionIncomeExpenseRecordService extends IService<UnionIncomeExpenseRecord> {
    /**
     * 根据商家id和收支类型，统计收支金额（该商家的总收入金额或总支出金额）
     * @param busId
     * @param type
     * @return
     * @throws Exception
     */
	Double sumMoneyByBusIdAndType(Integer busId, Integer type) throws Exception;

    /**
     * 根据商家id获取收支盈利金额（总收入金额-总收支金额）
     * @param busId
     * @return
     * @throws Exception
     */
	Double getProfitMoneyByBusId(Integer busId) throws Exception;

    /**
     * 根据联盟id、商家id和收支类型，统计收支金额（该商家在该联盟的总收入金额或总支出金额）
     * @param unionId
     * @param busId
     * @param type
     * @return
     * @throws Exception
     */
	Double sumMoneyByUnionIdAndBusIdAndType(Integer unionId, Integer busId, Integer type) throws Exception;

    /**
     * 根据联盟id和商家id获取收支盈利金额（总收入金额-总收支金额）
     * @param unionId
     * @param busId
     * @return
     * @throws Exception
     */
	Double getProfitMoneyByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception;
}
