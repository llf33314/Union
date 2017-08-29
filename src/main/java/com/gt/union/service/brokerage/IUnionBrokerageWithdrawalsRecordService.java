package com.gt.union.service.brokerage;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.brokerage.UnionBrokerageWithdrawalsRecord;

/**
 * <p>
 * 联盟佣金提现记录 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
public interface IUnionBrokerageWithdrawalsRecordService extends IService<UnionBrokerageWithdrawalsRecord> {
    /**
     * 根据商家id，统计历史提现金额总和
     * @param busId
     * @return
     * @throws Exception
     */
    Double sumMoneyByBusId(Integer busId) throws Exception;

    /**
     * 根据联盟id和商家id，统计历史提现金额总和
     * @param unionId
     * @param busId
     * @return
     * @throws Exception
     */
    Double sumMoneyByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception;

    /**
     * 根据商家id，分页获取提现记录信息
     * @param page
     * @param busId
     * @return
     * @throws Exception
     */
    Page pageMapByBusId(Page page, Integer busId) throws Exception;

    /**
     * 根据联盟id、商家id，分页获取提现记录信息
     * @param page
     * @param unionId
     * @param busId
     * @return
     * @throws Exception
     */
    Page pageMapByUnionIdAndBusId(Page page, Integer unionId, Integer busId) throws Exception;
}
