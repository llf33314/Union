package com.gt.union.opportunity.main.service;

import com.baomidou.mybatisplus.service.IService;
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
     * 根据id，获取对象
     *
     * @param id 商机id
     * @return UnionOpportunity
     * @throws Exception 统一处理异常
     */
    UnionOpportunity getById(Integer id) throws Exception;

    /**
     * 根据商机id、联盟id和接收盟员id，获取商机信息
     *
     * @param opportunityId 商机id
     * @param unionId       联盟id
     * @param toMemberId    接收盟员id
     * @return UnionOpportunity
     * @throws Exception 统一处理异常
     */
    UnionOpportunity getByIdAndUnionIdAndToMemberId(Integer opportunityId, Integer unionId, Integer toMemberId) throws Exception;

    /**
     * 获取商机佣金统计数据
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return OpportunityStatisticsVO
     * @throws Exception 统一处理异常
     */
    OpportunityStatisticsVO getOpportunityStatisticsVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：获取我的商机
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
     * 根据接收盟员id列表，获取商机信息
     *
     * @param toMemberIdList      接收盟员id列表
     * @param optAcceptStatusList 受理状态列表
     * @param optClientName       客户名称
     * @param optClientPhone      客户电话
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listByToMemberIdList(List<Integer> toMemberIdList, List<Integer> optAcceptStatusList, String optClientName, String optClientPhone) throws Exception;

    /**
     * 分页：获取我的商机推荐
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
     * 根据商机推荐盟员id列表，获取商机信息
     *
     * @param fromMemberIdList    商机推荐盟员id列表
     * @param optAcceptStatusList 受理状态列表
     * @param optClientName       客户名称
     * @param optClientPhone      客户电话
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listByFromMemberIdList(List<Integer> fromMemberIdList, List<Integer> optAcceptStatusList, String optClientName, String optClientPhone) throws Exception;

    /**
     * 根据接收盟员id和联盟id，获取受理成功的商机信息
     *
     * @param toMemberId 接收盟员id
     * @param unionId    联盟id
     * @param optIsPay   是否已支付(0：否 1：是)
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listAcceptConfirmedByToMemberIdAndUnionId(Integer toMemberId, Integer unionId, Integer optIsPay) throws Exception;

    /**
     * 根据推荐盟员id和联盟id，获取受理成功的商机信息
     *
     * @param fromMemberId 推荐盟员id
     * @param unionId      联盟id
     * @param optIsPay     是否已支付(0：否 1：是)
     * @return List<UnionOpportunity>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunity> listAcceptConfirmedByFromMemberIdAndUnionId(Integer fromMemberId, Integer unionId, Integer optIsPay) throws Exception;


    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 推荐商机
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
     * 接受或拒绝商机推荐
     *
     * @param opportunityId 商机id
     * @param unionId       联盟id
     * @param busId         商家id
     * @param isAccept      是否接受(0:否 1:是)
     * @param acceptPrice   受理金额，接受时必填
     * @throws Exception 统一处理异常
     */
    void updateStatusByIdAndUnionIdAndBusId(Integer opportunityId, Integer unionId, Integer busId, Integer isAccept, Double acceptPrice) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

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

}