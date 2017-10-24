package com.gt.union.brokerage.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.member.entity.UnionMember;

import java.util.List;

/**
 * 佣金提现记录 服务接口
 *
 * @author linweicong
 * @version 2017-10-23 15:28:54
 */
public interface IUnionBrokerageWithdrawalService extends IService<UnionBrokerageWithdrawal> {

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    /**
     * 根据盟员列表查询总提现金额
     *
     * @param members 盟员列表
     * @return Double
     */
    Double withdrawalSumByMemberIds(List<UnionMember> members);

    /**
     * 获取我已提现的佣金总和
     *
     * @param busId 商家id
     * @return double
     */
    double getSumWithdrawalsUnionBrokerage(Integer busId);

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    /**
     * 根据盟员列表查询支付的提现的金额列表
     *
     * @param members 盟员列表
     * @return List<UnionBrokerageWithdrawal>
     */
    List<UnionBrokerageWithdrawal> listWithdrawalByMemberIds(List<UnionMember> members);

    /**
     * 获取提现记录列表
     *
     * @param page  分页对象
     * @param busId 商家id
     * @return Page
     */
    Page listWithdrawals(Page page, Integer busId);

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    //******************************************* Object As a Service - list *******************************************

    //******************************************* Object As a Service - save *******************************************

    //******************************************* Object As a Service - remove *****************************************

    //******************************************* Object As a Service - update *****************************************

    //***************************************** Object As a Service - cache support ************************************

}
