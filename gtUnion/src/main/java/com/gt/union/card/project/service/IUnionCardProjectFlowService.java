package com.gt.union.card.project.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.project.entity.UnionCardProjectFlow;

import java.util.List;

/**
 * 活动项目流程 服务接口
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
public interface IUnionCardProjectFlowService extends IService<UnionCardProjectFlow> {
    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 根据项目id，获取审核记录
     *
     * @param projectId 项目id
     * @return List<UnionCardProjectFlow>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectFlow> listByProjectId(Integer projectId) throws Exception;

    /**
     * 我的联盟-联盟卡设置-活动卡设置-分页数据-我的活动项目-审批记录
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return List<UnionCardProjectFlow>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectFlow> listByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存
     *
     * @param newUnionCardProjectFlow 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionCardProjectFlow newUnionCardProjectFlow) throws Exception;

    /**
     * 批量保存
     *
     * @param saveFlowList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionCardProjectFlow> saveFlowList) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    /**
     * 批量删除
     *
     * @param idList id列表
     * @throws Exception 统一处理异常
     */
    void removeBatchById(List<Integer> idList) throws Exception;

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}