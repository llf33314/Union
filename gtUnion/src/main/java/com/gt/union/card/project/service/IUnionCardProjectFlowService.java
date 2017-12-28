package com.gt.union.card.project.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.project.entity.UnionCardProjectFlow;

import java.util.List;

/**
 * 活动项目流程 服务接口
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
public interface IUnionCardProjectFlowService {
    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionCardProjectFlowList 数据源
     * @param delStatus                删除状态
     * @return List<UnionCardProjectFlow>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectFlow> filterByDelStatus(List<UnionCardProjectFlow> unionCardProjectFlowList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取项目流程信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardProjectFlow
     * @throws Exception 统一处理异常
     */
    UnionCardProjectFlow getById(Integer id) throws Exception;

    /**
     * 获取未删除的项目流程信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardProjectFlow
     * @throws Exception 统一处理异常
     */
    UnionCardProjectFlow getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的项目流程信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardProjectFlow
     * @throws Exception 统一处理异常
     */
    UnionCardProjectFlow getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionCardProjectFlowList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionCardProjectFlow> unionCardProjectFlowList) throws Exception;


    /**
     * 获取项目流程列表信息(by myBatisGenerator)
     *
     * @param projectId projectId
     * @return List<UnionCardProjectFlow>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectFlow> listByProjectId(Integer projectId) throws Exception;

    /**
     * 获取未删除的项目流程列表信息(by myBatisGenerator)
     *
     * @param projectId projectId
     * @return List<UnionCardProjectFlow>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectFlow> listValidByProjectId(Integer projectId) throws Exception;

    /**
     * 获取已删除的项目流程列表信息(by myBatisGenerator)
     *
     * @param projectId projectId
     * @return List<UnionCardProjectFlow>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectFlow> listInvalidByProjectId(Integer projectId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionCardProjectFlow>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectFlow> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionCardProjectFlow> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionCardProjectFlow 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionCardProjectFlow newUnionCardProjectFlow) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionCardProjectFlowList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionCardProjectFlow> newUnionCardProjectFlowList) throws Exception;

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
     * @param updateUnionCardProjectFlow 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionCardProjectFlow updateUnionCardProjectFlow) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionCardProjectFlowList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionCardProjectFlow> updateUnionCardProjectFlowList) throws Exception;

    // TODO

    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

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


    //***************************************** Domain Driven Design - remove ******************************************


    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}