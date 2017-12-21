package com.gt.union.opportunity.brokerage.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageWithdrawal;

import java.util.List;

/**
 * 佣金提现 服务接口
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
public interface IUnionBrokerageWithdrawalService extends IService<UnionBrokerageWithdrawal> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取可提现金额
     *
     * @param busId 商家id
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double getAvailableMoneyByBusId(Integer busId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取提现列表信息
     *
     * @param busId 商家id
     * @return List<UnionBrokerageWithdrawal>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageWithdrawal> listByBusId(Integer busId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存
     *
     * @param newUnionBrokerageWithdrawal 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionBrokerageWithdrawal newUnionBrokerageWithdrawal) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 统计已提现金额
     *
     * @param busId 商家id
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumMoneyByBusId(Integer busId) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

}