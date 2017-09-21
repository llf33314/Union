package com.gt.union.brokerage.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.member.entity.UnionMember;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 佣金收入 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionBrokerageIncomeService extends IService<UnionBrokerageIncome> {

    /**
     * 根据商机id获取佣金收入
     *
     * @param id
     * @return
     */
    UnionBrokerageIncome getByUnionOpportunityId(Integer id);

    /**
     * 根据商家id和盟员身份id，分页获取同一个联盟下的售卡佣金分成列表信息，并根据售卡类型(精确匹配)、卡号(模糊匹配)进行匹配
     *
     * @param page             {not null} 分页对象
     * @param busId            {not null} 商家id
     * @param memberId         {not null} 盟员身份id
     * @param optionCardType   可选项 售卡类型
     * @param optionCardNumber 可选项 卡号
     * @param optionBeginDate        可选项 开始日期
     * @param optionEndDate          可选项 结束日期
     * @return
     * @throws Exception
     */
    Page pageCardMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId, Integer optionCardType
            , String optionCardNumber, String optionBeginDate, String optionEndDate) throws Exception;

    /**
     * 获取财务可提现佣金详情列表
     *
     * @param busId 商家id
     * @return
     */
    Map<String, Object> listAbleToWithdrawalBrokerage(Integer busId) throws Exception;

    /**
     * 该商家可提现金额总和
     *
     * @param busId 商家id
     * @return
     */
    Double withdrawalSum(Integer busId) throws Exception;

    /**
     * 通过盟员id列表查询收入总和
     *
     * @param members
     * @return
     */
    Double withdrawalSumByMemberIds(List<UnionMember> members);

    /**
     * 获取我获得的总佣金之和（已支付的）
     *
     * @param busId
     * @return
     */
    double getSumInComeUnionBrokerage(Integer busId);

    /**
     * 根据佣金所得类型获取该类型佣金总和
     *
     * @param busId 商家id
     * @param type  类型 1：售卡 2：商机
     * @return
     */
    double getSumInComeUnionBrokerageByType(Integer busId, int type);
}
