package com.gt.union.preferential.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.preferential.entity.UnionPreferentialItem;
import com.gt.union.preferential.entity.UnionPreferentialProject;

import java.util.List;
import java.util.Map;

/**
 * 优惠项目 服务接口
 *
 * @author linweicong
 * @version 2017-10-23 14:51:10
 */
public interface IUnionPreferentialProjectService extends IService<UnionPreferentialProject> {
    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    /**
     * 根据盟员身份id获取优惠项目
     *
     * @param memberId {not null} 盟员身份id
     * @return UnionPreferentialProject\
     * @throws Exception 全局处理异常
     */
    UnionPreferentialProject getByMemberId(Integer memberId) throws Exception;

    /**
     * 根据商家id、盟员身份id、优惠项目id和优惠服务状态，获取详情信息
     *
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param projectId  {not null} 优惠项目id
     * @param itemStatus {not null} 优惠服务状态
     * @return Map <String, Object>
     * @throws Exception 全局处理异常
     */
    Map<String, Object> getDetailByBusIdAndMemberIdAndProjectIdAndItemStatus(Integer busId, Integer memberId
            , Integer projectId, Integer itemStatus) throws Exception;

    /**
     * 根据商家id和盟员身份id，获取优惠项目信息
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return UnionPreferentialProject
     * @throws Exception 全局处理异常
     */
    UnionPreferentialProject getByBusIdAndMemberId(Integer busId, Integer memberId) throws Exception;

    /**
     * 根据商家id和盟员身份id，获取优惠项目信息
     *
     * @param page     {not null} 分页对象
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return Map<String, Object>
     * @throws Exception 全局处理异常
     */
    Map<String, Object> getPageMapByBusIdAndMemberId(Page<UnionPreferentialItem> page, Integer busId, Integer memberId) throws Exception;

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    /**
     * 根据商家id、盟员身份id和优惠服务项状态，分页查询优惠项目列表信息
     *
     * @param page       {not null} 分页对象
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param itemStatus {not null} 优惠服务项状态
     * @return Page
     * @throws Exception 全局处理异常
     */
    Page pageMapByBusIdAndMemberIdAndItemStatus(Page page, Integer busId, Integer memberId, Integer itemStatus) throws Exception;

    /**
     * 获取所有过期的优惠项目列表信息，即项目所属盟员已退盟
     *
     * @return List<UnionPreferentialProject>
     * @throws Exception 全局处理异常
     */
    List<UnionPreferentialProject> listExpired() throws Exception;

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    /**
     * 根据优惠项目id、商家id和盟员身份id，更新优惠项目说明
     *
     * @param projectId    {not null} 优惠项目id
     * @param busId        {not null} 商家id
     * @param memberId     {not null} 盟员身份id
     * @param illustration {not null} 优惠项目说明
     * @throws Exception 全局处理异常
     */
    void updateIllustrationByIdAndBusIdAndMemberId(Integer projectId, Integer busId, Integer memberId, String illustration) throws Exception;

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    /**
     * 根据商家id、盟员身份id和优惠服务项状态，统计优惠服务数
     *
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param itemStatus {not null} 优惠服务状态
     * @return Integer
     * @throws Exception 全局处理异常
     */
    Integer countByBusInAndMemberIdAndItemStatus(Integer busId, Integer memberId, Integer itemStatus) throws Exception;

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    /**
     * 根据id查询对象
     *
     * @param projectId 对象id
     * @return UnionPreferentialProject
     * @throws Exception 全局处理异常
     */
    UnionPreferentialProject getById(Integer projectId) throws Exception;

    //******************************************* Object As a Service - list *******************************************

    /**
     * 根据优惠项目所属的盟员身份id查询对象列表
     *
     * @param memberId 优惠项目所属的盟员身份id
     * @return List<UnionPreferentialProject>
     * @throws Exception 全局处理异常
     */
    List<UnionPreferentialProject> listByMemberId(Integer memberId) throws Exception;

    //******************************************* Object As a Service - save *******************************************

    /**
     * 新增对象
     *
     * @param newProject 新增的对象
     * @throws Exception 全局处理异常
     */
    void save(UnionPreferentialProject newProject) throws Exception;

    /**
     * 批量新增对象
     *
     * @param newProjectList 批量新增的对象列表
     * @throws Exception 全局处理异常
     */
    void saveBatch(List<UnionPreferentialProject> newProjectList) throws Exception;

    //******************************************* Object As a Service - remove *****************************************

    /**
     * 根据id删除对象
     *
     * @param projectId 对象id
     * @throws Exception 全局处理异常
     */
    void removeById(Integer projectId) throws Exception;

    /**
     * 根据id列表批量删除对象
     *
     * @param projectIdList 对象id列表
     * @throws Exception 全局处理异常
     */
    void removeBatchById(List<Integer> projectIdList) throws Exception;

    //******************************************* Object As a Service - update *****************************************

    /**
     * 修改对象
     *
     * @param updateProject 修改的对象
     * @throws Exception 全局处理异常
     */
    void update(UnionPreferentialProject updateProject) throws Exception;

    /**
     * 批量修改对象
     *
     * @param updateProjectList 批量修改的对象列表
     * @throws Exception 全局处理异常
     */
    void updateBatch(List<UnionPreferentialProject> updateProjectList) throws Exception;
}
