package com.gt.union.opportunity.main.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.opportunity.brokerage.vo.BrokerageOpportunityVO;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.opportunity.main.vo.OpportunityStatisticsVO;
import com.gt.union.opportunity.main.vo.OpportunityVO;

import java.util.Date;
import java.util.List;

/**
 * 商机 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 16:56:17
 */
public interface IUnionOpportunityService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 获取未删除的商机信息
     *
     * @param opportunityId 商机id
     * @param unionId       联盟id
     * @param toMemberId    接收者盟员id
     * @return UnionOpportunity
     * @throws Exception 统一处理异常
     */
    UnionOpportunity getValidByIdAndUnionIdAndToMemberId(Integer opportunityId, Integer unionId, Integer toMemberId) throws Exception;

    /**
     * 商机-数据统计图
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return OpportunityStatisticsVO
     * @throws Exception 统一处理异常
     */
    OpportunityStatisticsVO getOpportunityStatisticsVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //********************************************* Base On Business - list ********************************************

    /**
     * 获取未删除的商机列表信息
     *
     * @param fromMemberIdList 商机推荐盟员id列表
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByFromMemberIdList(List<Integer> fromMemberIdList) throws Exception;

    /**
     * 获取未删除的商机列表信息
     *
     * @param toMemberIdList 接收盟员id列表
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByToMemberIdList(List<Integer> toMemberIdList) throws Exception;

    /**
     * 获取未删除的商机列表信息
     *
     * @param fromMemberIdList 商机推荐者盟员id列表
     * @param acceptStatus     受理状态
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByFromMemberIdListAndAcceptStatus(List<Integer> fromMemberIdList, Integer acceptStatus) throws Exception;

    /**
     * 获取未删除的商机列表信息
     *
     * @param fromMemberIdList 商机推荐者盟员id列表
     * @param acceptStatus     受理状态
     * @param isClose          是否已结算(0:否 1:是)
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByFromMemberIdListAndAcceptStatusAndIsClose(List<Integer> fromMemberIdList, Integer acceptStatus, Integer isClose) throws Exception;

    /**
     * 获取未删除的商机列表信息
     *
     * @param toMemberIdList 商机接受者盟员id列表
     * @param acceptStatus   受理状态
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByToMemberIdListAndAcceptStatus(List<Integer> toMemberIdList, Integer acceptStatus) throws Exception;

    /**
     * 获取未删除的商机列表信息
     *
     * @param toMemberIdList 商机接受者盟员id列表
     * @param acceptStatus   受理状态
     * @param isClose        是否已结算(0:否 1:是)
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByToMemberIdListAndAcceptStatusAndIsClose(List<Integer> toMemberIdList, Integer acceptStatus, Integer isClose) throws Exception;

    /**
     * 获取未删除的商机列表信息
     *
     * @param unionId      联盟id
     * @param fromMemberId 推荐盟员id
     * @param acceptStatus 受理状态
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByUnionIdAndFromMemberIdAndAcceptStatus(Integer unionId, Integer fromMemberId, Integer acceptStatus) throws Exception;

    /**
     * 获取未删除的商机列表信息
     *
     * @param unionId          联盟id
     * @param fromMemberIdList 推荐盟员id列表
     * @param acceptStatus     受理状态
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByUnionIdAndFromMemberIdListAndAcceptStatus(Integer unionId, List<Integer> fromMemberIdList, Integer acceptStatus) throws Exception;

    /**
     * 获取未删除的商机列表信息
     *
     * @param unionId      联盟id
     * @param toMemberId   接收盟员id
     * @param acceptStatus 受理状态
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByUnionIdAndToMemberIdAndAcceptStatus(Integer unionId, Integer toMemberId, Integer acceptStatus) throws Exception;

    /**
     * 获取未删除的商机列表信息
     *
     * @param unionId        联盟id
     * @param toMemberIdList 接收盟员id列表
     * @param acceptStatus   受理状态
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByUnionIdAndToMemberIdListAndAcceptStatus(Integer unionId, List<Integer> toMemberIdList, Integer acceptStatus) throws Exception;


    /**
     * 获取未删除的商机列表信息
     *
     * @param unionId      联盟id
     * @param toMemberId   接收盟员id
     * @param acceptStatus 受理状态
     * @param isClose      是否已结算
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByUnionIdAndToMemberIdAndAcceptStatusAndIsClose(Integer unionId, Integer toMemberId, Integer acceptStatus, Integer isClose) throws Exception;

    /**
     * 分页：商机-我要推荐
     *
     * @param busId           商家id
     * @param optUnionId      联盟id
     * @param optAcceptStatus 受理状态
     * @param optClientName   客户名称
     * @param optClientPhone  客户电话
     * @return List<OpportunityVO>
     * @throws Exception 统一处理异常
     */
    List<OpportunityVO> listFromMeByBusId(Integer busId, Integer optUnionId, String optAcceptStatus, String optClientName, String optClientPhone) throws Exception;

    /**
     * 分页：商机-我的商机
     *
     * @param busId           商家id
     * @param optUnionId      联盟id
     * @param optAcceptStatus 受理状态
     * @param optClientName   客户名称
     * @param optClientPhone  客户电话
     * @return List<OpportunityVO>
     * @throws Exception 统一处理异常
     */
    List<OpportunityVO> listToMeByBusId(Integer busId, Integer optUnionId, String optAcceptStatus, String optClientName, String optClientPhone) throws Exception;

    /**
     * 重复方法抽离：根据商机推荐列表获取VO对象，并按是否结算排序(未结算>已结算)、时间倒序排序
     *
     * @param opportunityList 商机推荐列表
     * @return List<BrokerageOpportunityVO>
     * @throws Exception 统一处理异常
     */
    List<BrokerageOpportunityVO> listBrokerageOpportunityVO(List<UnionOpportunity> opportunityList) throws Exception;

    //********************************************* Base On Business - save ********************************************

    /**
     * 商机-我要推荐-我要推荐
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param vo      表单内容
     * @throws Exception 统一处理异常
     */
    void saveOpportunityVOByBusIdAndUnionId(Integer busId, Integer unionId, OpportunityVO vo) throws Exception;

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    /**
     * 商机-我的商机-分页数据-接受或拒绝
     *
     * @param busId         商家id
     * @param opportunityId 商机id
     * @param unionId       联盟id
     * @param isAccept      是否接受(0:否 1:是)
     * @param acceptPrice   受理金额，接受时必填
     * @throws Exception 统一处理异常
     */
    void updateByBusIdAndIdAndUnionId(Integer busId, Integer opportunityId, Integer unionId, Integer isAccept, Double acceptPrice) throws Exception;

    //********************************************* Base On Business - other *******************************************

    /**
     * 统计未删除的佣金金额总和
     *
     * @param fromMemberIdList 商机推荐者盟员id列表
     * @param acceptStatus     受理状态
     * @param isClose          是否已结算(0:否 1:是)
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumValidBrokerageMoneyByFromMemberIdListAndAcceptStatusAndIsClose(List<Integer> fromMemberIdList, Integer acceptStatus, Integer isClose) throws Exception;

    /**
     * 统计未删除的佣金金额总和
     *
     * @param toMemberIdList 商机接受者盟员id列表
     * @param acceptStatus   受理状态
     * @param isClose        是否已结算(0:否 1:是)
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumValidBrokerageMoneyByToMemberIdListAndAcceptStatusAndIsClose(List<Integer> toMemberIdList, Integer acceptStatus, Integer isClose) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionOpportunityList 数据源
     * @param delStatus            删除状态
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> filterByDelStatus(List<UnionOpportunity> unionOpportunityList, Integer delStatus) throws Exception;

    /**
     * 根据联盟id进行过滤
     *
     * @param opportunityList 数据源
     * @param unionId         联盟id
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> filterByUnionId(List<UnionOpportunity> opportunityList, Integer unionId) throws Exception;

    /**
     * 根据受理状态进行过滤
     *
     * @param opportunityList 数据源
     * @param acceptStatus    受理状态
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> filterByAcceptStatus(List<UnionOpportunity> opportunityList, Integer acceptStatus) throws Exception;

    /**
     * 根据是否已结算进行过滤
     *
     * @param opportunityList 数据源
     * @param isClose         是否已结算
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> filterByIsClose(List<UnionOpportunity> opportunityList, Integer isClose) throws Exception;

    /**
     * 根据创建时间范围进行过滤
     *
     * @param opportunityList 数据源
     * @param optBeginTime    开始时间
     * @param optEndTime      结束时间
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> filterBetweenTime(List<UnionOpportunity> opportunityList, Date optBeginTime, Date optEndTime) throws Exception;

    /**
     * 根据受理状态进行过滤
     *
     * @param opportunityList  数据源
     * @param acceptStatusList 受理状态列表
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> filterByAcceptStatusList(List<UnionOpportunity> opportunityList, List<Integer> acceptStatusList) throws Exception;

    /**
     * 根据客户名称进行模糊过滤
     *
     * @param opportunityList 数据源
     * @param likeClientName  客户名称
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> filterByLikeClientName(List<UnionOpportunity> opportunityList, String likeClientName) throws Exception;

    /**
     * 根据客户电话进行模糊过滤
     *
     * @param opportunityList 数据源
     * @param likeClientPhone 客户电话
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> filterByLikeClientPhone(List<UnionOpportunity> opportunityList, String likeClientPhone) throws Exception;

    /**
     * 根据商机推荐者盟员id进行过滤
     *
     * @param opportunityList 数据源
     * @param fromMemberId    商机推荐者盟员id
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */

    List<UnionOpportunity> filterByFromMemberId(List<UnionOpportunity> opportunityList, Integer fromMemberId) throws Exception;

    /**
     * 根据商机接受者盟员id进行过滤
     *
     * @param opportunityList 数据源
     * @param toMemberId      商机接受者盟员id
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> filterByToMemberId(List<UnionOpportunity> opportunityList, Integer toMemberId) throws Exception;

    /**
     * 过滤掉推荐者是已退盟状态的商机
     *
     * @param opportunityList 数据源
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> filterInvalidFromMemberId(List<UnionOpportunity> opportunityList) throws Exception;

    /**
     * 过滤掉接受者是已退盟状态的商机
     *
     * @param opportunityList 数据源
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> filterInvalidToMemberId(List<UnionOpportunity> opportunityList) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取商机信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionOpportunity
     * @throws Exception 统一处理异常
     */
    UnionOpportunity getById(Integer id) throws Exception;

    /**
     * 获取未删除的商机信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionOpportunity
     * @throws Exception 统一处理异常
     */
    UnionOpportunity getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的商机信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionOpportunity
     * @throws Exception 统一处理异常
     */
    UnionOpportunity getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionOpportunityList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionOpportunity> unionOpportunityList) throws Exception;


    /**
     * 获取商机列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取未删除的商机列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取已删除的商机列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listInvalidByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取商机列表信息(by myBatisGenerator)
     *
     * @param toMemberId toMemberId
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listByToMemberId(Integer toMemberId) throws Exception;

    /**
     * 获取未删除的商机列表信息(by myBatisGenerator)
     *
     * @param toMemberId toMemberId
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByToMemberId(Integer toMemberId) throws Exception;

    /**
     * 获取已删除的商机列表信息(by myBatisGenerator)
     *
     * @param toMemberId toMemberId
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listInvalidByToMemberId(Integer toMemberId) throws Exception;

    /**
     * 获取商机列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的商机列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的商机列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionOpportunity> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionOpportunity 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionOpportunity newUnionOpportunity) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionOpportunityList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionOpportunity> newUnionOpportunityList) throws Exception;

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
     * @param updateUnionOpportunity 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionOpportunity updateUnionOpportunity) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionOpportunityList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionOpportunity> updateUnionOpportunityList) throws Exception;

}