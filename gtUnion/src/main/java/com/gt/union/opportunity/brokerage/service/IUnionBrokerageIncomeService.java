package com.gt.union.opportunity.brokerage.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.opportunity.brokerage.vo.BrokerageOpportunityVO;

import java.util.List;

/**
 * 佣金收入 服务接口
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
public interface IUnionBrokerageIncomeService extends IService<UnionBrokerageIncome> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取商机佣金收入信息
     *
     * @param unionId       联盟id
     * @param memberId      盟员id
     * @param opportunityId 商机id
     * @return boolean
     * @throws Exception 统一处理异常
     */
    UnionBrokerageIncome getByUnionIdAndMemberIdAndOpportunityId(Integer unionId, Integer memberId, Integer opportunityId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 佣金结算-我的佣金收入-分页
     *
     * @param busId          商家id
     * @param optUnionId     联盟id
     * @param optToMemberId  接收盟员id
     * @param optIsClose     是否已结算(0:否 1:是)
     * @param optClientName  客户名称
     * @param optClientPhone 客户电话
     * @return List<BrokerageOpportunityVO>
     * @throws Exception 统一处理异常
     */
    List<BrokerageOpportunityVO> listBrokerageOpportunityVOByBusId(Integer busId, Integer optUnionId, Integer optToMemberId,
                                                                   Integer optIsClose, String optClientName, String optClientPhone) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 批量保存
     *
     * @param newUnionBrokerageIncomeList 内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionBrokerageIncome> newUnionBrokerageIncomeList) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    /**
     * 判断商机佣金收入是否存在
     *
     * @param unionId       联盟id
     * @param memberId      盟员id
     * @param opportunityId 商机id
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existByUnionIdAndMemberIdAndOpportunityId(Integer unionId, Integer memberId, Integer opportunityId) throws Exception;

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据联盟id进行过滤
     *
     * @param incomeList 数据源
     * @param unionId    联盟id
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> filterByUnionId(List<UnionBrokerageIncome> incomeList, Integer unionId) throws Exception;

    /**
     * 根据商机id进行过滤
     *
     * @param incomeList    数据源
     * @param opportunityId 商机id
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> filterByOpportunityId(List<UnionBrokerageIncome> incomeList, Integer opportunityId) throws Exception;

}