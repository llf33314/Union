package com.gt.union.opportunity.brokerage.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageIncome;

import java.util.List;

/**
 * 佣金收入 服务接口
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
public interface IUnionBrokerageIncomeService {
    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    /**
     * 分页：商机-佣金结算-我的佣金收入
     *
     * @param page           分页对象
     * @param busId          商家id
     * @param optUnionId     联盟id
     * @param optToMemberId  接收盟员id
     * @param optIsClose     是否已结算(0:否 1:是)
     * @param optClientName  客户名称
     * @param optClientPhone 客户电话
     * @return List<BrokerageOpportunityVO>
     * @throws Exception 统一处理异常
     */
    Page pageBrokerageOpportunityVOByBusId(
        Page page, Integer busId, Integer optUnionId, Integer optToMemberId,
        Integer optIsClose, String optClientName, String optClientPhone) throws Exception;

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    /**
     * 统计未删除的佣金收入总和
     *
     * @param busId 商家id
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumValidMoneyByBusId(Integer busId) throws Exception;

    /**
     * 统计未删除的佣金收入总和
     *
     * @param busId 商家id
     * @param type  类型(1:售卡 2:商机)
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumValidMoneyByBusIdAndType(Integer busId, Integer type) throws Exception;

    /**
     * 统计未删除的佣金收入总和
     *
     * @param busId        商家id
     * @param type         类型(1:售卡 2:商机)
     * @param memberIdList 盟员id列表
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumValidMoneyByBusIdAndTypeAndMemberIdList(Integer busId, Integer type, List<Integer> memberIdList) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionBrokerageIncomeList 数据源
     * @param delStatus                删除状态
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> filterByDelStatus(List<UnionBrokerageIncome> unionBrokerageIncomeList, Integer delStatus) throws Exception;

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

    /**
     * 根据类型进行过滤
     *
     * @param incomeList 数据源
     * @param type       类型
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> filterByType(List<UnionBrokerageIncome> incomeList, Integer type) throws Exception;

    /**
     * 根据盟员id列表进行过滤
     *
     * @param incomeList   数据源
     * @param memberIdList 盟员id列表
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> filterByMemberIdList(List<UnionBrokerageIncome> incomeList, List<Integer> memberIdList) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取佣金收入信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionBrokerageIncome
     * @throws Exception 统一处理异常
     */
    UnionBrokerageIncome getById(Integer id) throws Exception;

    /**
     * 获取未删除的佣金收入信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionBrokerageIncome
     * @throws Exception 统一处理异常
     */
    UnionBrokerageIncome getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的佣金收入信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionBrokerageIncome
     * @throws Exception 统一处理异常
     */
    UnionBrokerageIncome getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionBrokerageIncomeList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionBrokerageIncome> unionBrokerageIncomeList) throws Exception;


    /**
     * 获取佣金收入列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listByBusId(Integer busId) throws Exception;

    /**
     * 获取未删除的佣金收入列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listValidByBusId(Integer busId) throws Exception;

    /**
     * 获取已删除的佣金收入列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listInvalidByBusId(Integer busId) throws Exception;

    /**
     * 获取佣金收入列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listByMemberId(Integer memberId) throws Exception;

    /**
     * 获取未删除的佣金收入列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listValidByMemberId(Integer memberId) throws Exception;

    /**
     * 获取已删除的佣金收入列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listInvalidByMemberId(Integer memberId) throws Exception;

    /**
     * 获取佣金收入列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的佣金收入列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的佣金收入列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取佣金收入列表信息(by myBatisGenerator)
     *
     * @param cardId cardId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listByCardId(Integer cardId) throws Exception;

    /**
     * 获取未删除的佣金收入列表信息(by myBatisGenerator)
     *
     * @param cardId cardId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listValidByCardId(Integer cardId) throws Exception;

    /**
     * 获取已删除的佣金收入列表信息(by myBatisGenerator)
     *
     * @param cardId cardId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listInvalidByCardId(Integer cardId) throws Exception;

    /**
     * 获取佣金收入列表信息(by myBatisGenerator)
     *
     * @param opportunityId opportunityId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listByOpportunityId(Integer opportunityId) throws Exception;

    /**
     * 获取未删除的佣金收入列表信息(by myBatisGenerator)
     *
     * @param opportunityId opportunityId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listValidByOpportunityId(Integer opportunityId) throws Exception;

    /**
     * 获取已删除的佣金收入列表信息(by myBatisGenerator)
     *
     * @param opportunityId opportunityId
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listInvalidByOpportunityId(Integer opportunityId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionBrokerageIncome>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageIncome> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionBrokerageIncome> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionBrokerageIncome 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionBrokerageIncome newUnionBrokerageIncome) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionBrokerageIncomeList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionBrokerageIncome> newUnionBrokerageIncomeList) throws Exception;

    //****************************************** Object As a Service - remove ******************************************

    /**
     * 移除(by myBatisGenerator)
     *
     * @param id 移除内容
     * @throws Exception 统一处理异常
     */
    void removeById(Integer id) throws Exception;

    /**
     * 批量移除(by myBatisGenerator)
     *
     * @param idList 移除内容
     * @throws Exception 统一处理异常
     */
    void removeBatchById(List<Integer> idList) throws Exception;

    //****************************************** Object As a Service - update ******************************************

    /**
     * 更新(by myBatisGenerator)
     *
     * @param updateUnionBrokerageIncome 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionBrokerageIncome updateUnionBrokerageIncome) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionBrokerageIncomeList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionBrokerageIncome> updateUnionBrokerageIncomeList) throws Exception;

}
