package com.gt.union.opportunity.main.service;

import com.baomidou.mybatisplus.service.IService;
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
public interface IUnionOpportunityService extends IService<UnionOpportunity> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取商机信息
     *
     * @param id 商机id
     * @return UnionOpportunity
     * @throws Exception 统一处理异常
     */
    UnionOpportunity getById(Integer id) throws Exception;

    /**
     * 获取商机信息
     *
     * @param opportunityId 商机id
     * @param unionId       联盟id
     * @param toMemberId    接收者盟员id
     * @return UnionOpportunity
     * @throws Exception 统一处理异常
     */
    UnionOpportunity getByIdAndUnionIdAndToMemberId(Integer opportunityId, Integer unionId, Integer toMemberId) throws Exception;
    
    /**
     * 数据统计图
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return OpportunityStatisticsVO
     * @throws Exception 统一处理异常
     */
    OpportunityStatisticsVO getOpportunityStatisticsVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 获取商机id列表
     *
     * @param opportunityList 商机列表
     * @return List<Integer>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionOpportunity> opportunityList) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 我的商机-分页
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
     * 获取商机列表信息
     *
     * @param toMemberIdList 接收盟员id列表
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listByToMemberIdList(List<Integer> toMemberIdList) throws Exception;

    /**
     * 我要推荐-分页
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
     * 获取商机列表信息
     *
     * @param fromMemberIdList 商机推荐盟员id列表
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listByFromMemberIdList(List<Integer> fromMemberIdList) throws Exception;

    /**
     * 获取商机列表信息
     *
     * @param unionId      联盟id
     * @param toMemberId   接收盟员id
     * @param acceptStatus 受理状态
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listByUnionIdAndToMemberIdAndAcceptStatus(Integer unionId, Integer toMemberId, Integer acceptStatus) throws Exception;

    /**
     * 获取商机列表信息
     *
     * @param unionId      联盟id
     * @param toMemberId   接收盟员id
     * @param acceptStatus 受理状态
     * @param isClose      是否已结算
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listByUnionIdAndToMemberIdAndAcceptStatusAndIsClose(Integer unionId, Integer toMemberId, Integer acceptStatus, Integer isClose) throws Exception;


    /**
     * 获取商机列表信息
     *
     * @param unionId      联盟id
     * @param fromMemberId 推荐盟员id
     * @param acceptStatus 受理状态
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listByUnionIdAndFromMemberIdAndAcceptStatus(Integer unionId, Integer fromMemberId, Integer acceptStatus) throws Exception;

    /**
     * 重复方法抽离：根据商机推荐列表获取VO对象，并按是否结算排序(未结算>已结算)、时间倒序排序
     *
     * @param opportunityList 商机推荐列表
     * @return List<BrokerageOpportunityVO>
     * @throws Exception 统一处理异常
     */
    List<BrokerageOpportunityVO> listBrokerageOpportunityVO(List<UnionOpportunity> opportunityList) throws Exception;

    /**
     * 获取商机列表信息
     *
     * @param fromMemberIdList 商机推荐者盟员id列表
     * @param acceptStatus     受理状态
     * @param isClose          是否已结算(0:否 1:是)
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listByFromMemberIdListAndAcceptStatusAndIsClose(List<Integer> fromMemberIdList, Integer acceptStatus, Integer isClose) throws Exception;

    /**
     * 获取商机列表信息
     *
     * @param toMemberIdList 商机接受者盟员id列表
     * @param acceptStatus   受理状态
     * @param isClose        是否已结算(0:否 1:是)
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listByToMemberIdListAndAcceptStatusAndIsClose(List<Integer> toMemberIdList, Integer acceptStatus, Integer isClose) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 我要推荐-我要推荐
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param vo      表单内容
     * @throws Exception 统一处理异常
     */
    void saveOpportunityVOByBusIdAndUnionId(Integer busId, Integer unionId, OpportunityVO vo) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 我的商机-分页-接受或拒绝
     *
     * @param busId         商家id
     * @param opportunityId 商机id
     * @param unionId       联盟id
     * @param isAccept      是否接受(0:否 1:是)
     * @param acceptPrice   受理金额，接受时必填
     * @throws Exception 统一处理异常
     */
    void updateStatusByBusIdAndIdAndUnionId(Integer busId, Integer opportunityId, Integer unionId, Integer isAccept, Double acceptPrice) throws Exception;

    /**
     * 批量更新
     *
     * @param updateUnionOpportunityList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionOpportunity> updateUnionOpportunityList) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 统计佣金金额
     *
     * @param fromMemberIdList 商机推荐者盟员id列表
     * @param acceptStatus     受理状态
     * @param isClose          是否已结算(0:否 1:是)
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumBrokerageMoneyByFromMemberIdListAndAcceptStatusAndIsClose(List<Integer> fromMemberIdList, Integer acceptStatus, Integer isClose) throws Exception;

    /**
     * 统计佣金金额
     *
     * @param toMemberIdList 商机接受者盟员id列表
     * @param acceptStatus   受理状态
     * @param isClose        是否已结算(0:否 1:是)
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumBrokerageMoneyByToMemberIdListAndAcceptStatusAndIsClose(List<Integer> toMemberIdList, Integer acceptStatus, Integer isClose) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

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
     * 根据商机接受者盟员id进行过滤
     *
     * @param opportunityList 数据源
     * @param toMemberId      商机接受者盟员id
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> filterByToMemberId(List<UnionOpportunity> opportunityList, Integer toMemberId) throws Exception;

    /**
     * 根据商机推荐者盟员id进行过滤
     *
     * @param opportunityList 数据源
     * @param fromMemberId    商机推荐者盟员id
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> filterByFromMemberId(List<UnionOpportunity> opportunityList, Integer fromMemberId) throws Exception;

}