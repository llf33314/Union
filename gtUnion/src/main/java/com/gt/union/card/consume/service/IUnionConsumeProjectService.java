package com.gt.union.card.consume.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.consume.entity.UnionConsumeProject;

import java.util.List;

/**
 * 消费核销项目优惠 服务接口
 *
 * @author linweicong
 * @version 2017-11-27 10:27:29
 */
public interface IUnionConsumeProjectService {
    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    /**
     * 缓存穿透-统计未删除的消费优惠个数
     *
     * @param projectId     项目id
     * @param projectItemId 项目优惠id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countValidByProjectIdAndProjectItemId(Integer projectId, Integer projectItemId) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionConsumeProjectList 数据源
     * @param delStatus               删除状态
     * @return List<UnionConsumeProject>
     * @throws Exception 统一处理异常
     */
    List<UnionConsumeProject> filterByDelStatus(List<UnionConsumeProject> unionConsumeProjectList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取消费核销项目优惠信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionConsumeProject
     * @throws Exception 统一处理异常
     */
    UnionConsumeProject getById(Integer id) throws Exception;

    /**
     * 获取未删除的消费核销项目优惠信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionConsumeProject
     * @throws Exception 统一处理异常
     */
    UnionConsumeProject getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的消费核销项目优惠信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionConsumeProject
     * @throws Exception 统一处理异常
     */
    UnionConsumeProject getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionConsumeProjectList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionConsumeProject> unionConsumeProjectList) throws Exception;


    /**
     * 获取消费核销项目优惠列表信息(by myBatisGenerator)
     *
     * @param consumeId consumeId
     * @return List<UnionConsumeProject>
     * @throws Exception 统一处理异常
     */
    List<UnionConsumeProject> listByConsumeId(Integer consumeId) throws Exception;

    /**
     * 获取未删除的消费核销项目优惠列表信息(by myBatisGenerator)
     *
     * @param consumeId consumeId
     * @return List<UnionConsumeProject>
     * @throws Exception 统一处理异常
     */
    List<UnionConsumeProject> listValidByConsumeId(Integer consumeId) throws Exception;

    /**
     * 获取已删除的消费核销项目优惠列表信息(by myBatisGenerator)
     *
     * @param consumeId consumeId
     * @return List<UnionConsumeProject>
     * @throws Exception 统一处理异常
     */
    List<UnionConsumeProject> listInvalidByConsumeId(Integer consumeId) throws Exception;

    /**
     * 获取消费核销项目优惠列表信息(by myBatisGenerator)
     *
     * @param projectItemId projectItemId
     * @return List<UnionConsumeProject>
     * @throws Exception 统一处理异常
     */
    List<UnionConsumeProject> listByProjectItemId(Integer projectItemId) throws Exception;

    /**
     * 获取未删除的消费核销项目优惠列表信息(by myBatisGenerator)
     *
     * @param projectItemId projectItemId
     * @return List<UnionConsumeProject>
     * @throws Exception 统一处理异常
     */
    List<UnionConsumeProject> listValidByProjectItemId(Integer projectItemId) throws Exception;

    /**
     * 获取已删除的消费核销项目优惠列表信息(by myBatisGenerator)
     *
     * @param projectItemId projectItemId
     * @return List<UnionConsumeProject>
     * @throws Exception 统一处理异常
     */
    List<UnionConsumeProject> listInvalidByProjectItemId(Integer projectItemId) throws Exception;

    /**
     * 获取消费核销项目优惠列表信息(by myBatisGenerator)
     *
     * @param projectId projectId
     * @return List<UnionConsumeProject>
     * @throws Exception 统一处理异常
     */
    List<UnionConsumeProject> listByProjectId(Integer projectId) throws Exception;

    /**
     * 获取未删除的消费核销项目优惠列表信息(by myBatisGenerator)
     *
     * @param projectId projectId
     * @return List<UnionConsumeProject>
     * @throws Exception 统一处理异常
     */
    List<UnionConsumeProject> listValidByProjectId(Integer projectId) throws Exception;

    /**
     * 获取已删除的消费核销项目优惠列表信息(by myBatisGenerator)
     *
     * @param projectId projectId
     * @return List<UnionConsumeProject>
     * @throws Exception 统一处理异常
     */
    List<UnionConsumeProject> listInvalidByProjectId(Integer projectId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionConsumeProject>
     * @throws Exception 统一处理异常
     */
    List<UnionConsumeProject> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionConsumeProject> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionConsumeProject 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionConsumeProject newUnionConsumeProject) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionConsumeProjectList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionConsumeProject> newUnionConsumeProjectList) throws Exception;

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
     * @param updateUnionConsumeProject 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionConsumeProject updateUnionConsumeProject) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionConsumeProjectList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionConsumeProject> updateUnionConsumeProjectList) throws Exception;

}