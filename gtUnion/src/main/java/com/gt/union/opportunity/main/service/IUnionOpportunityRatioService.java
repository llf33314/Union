package com.gt.union.opportunity.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.opportunity.main.entity.UnionOpportunityRatio;
import com.gt.union.opportunity.main.vo.OpportunityRatioVO;

import java.util.List;

/**
 * 商机佣金比率 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 16:56:20
 */
public interface IUnionOpportunityRatioService extends IService<UnionOpportunityRatio> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取商机佣金比例信息
     *
     * @param unionId      联盟id
     * @param fromMemberId 设置佣金比率盟员id
     * @param toMemberId   受惠佣金比率盟员id
     * @return UnionOpportunityRatio
     * @throws Exception 统一处理异常
     */
    UnionOpportunityRatio getByUnionIdAndFromMemberIdAndToMemberId(Integer unionId, Integer fromMemberId, Integer toMemberId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：商机-商机佣金比设置
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<OpportunityRatioVO>
     * @throws Exception 统一处理异常
     */
    List<OpportunityRatioVO> listOpportunityRatioVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 商机-商机佣金比例设置-设置佣金比例-更新
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param toMemberId 受惠佣金比率盟员id
     * @param ratio      佣金比例
     * @throws Exception 统一处理异常
     */
    void updateRatioByBusIdAndUnionIdAndToMemberId(Integer busId, Integer unionId, Integer toMemberId, Double ratio) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据设置佣金比率盟员id进行过滤
     *
     * @param ratioList    数据源
     * @param fromMemberId 设置佣金比率盟员id
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> filterByFromMemberId(List<UnionOpportunityRatio> ratioList, Integer fromMemberId) throws Exception;

    /**
     * 根据受惠佣金比率盟员id进行过滤
     *
     * @param ratioList  数据源
     * @param toMemberId 设置佣金比率盟员id
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> filterByToMemberId(List<UnionOpportunityRatio> ratioList, Integer toMemberId) throws Exception;

    /**
     * 根据联盟id进行过滤
     *
     * @param ratioList 数据源
     * @param unionId   联盟id
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> filterByUnionId(List<UnionOpportunityRatio> ratioList, Integer unionId) throws Exception;

}