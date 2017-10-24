package com.gt.union.preferential.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.preferential.entity.UnionPreferentialItem;
import com.gt.union.preferential.entity.UnionPreferentialProject;

import java.util.List;

/**
 * 优惠服务项 服务接口
 *
 * @author linweicong
 * @version 2017-10-23 14:51:10
 */
public interface IUnionPreferentialItemService extends IService<UnionPreferentialItem> {
    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    /**
     * 根据优惠项目id，分页获取优惠服务列表信息
     *
     * @param page      {not null} 分页对象
     * @param projectId {not null} 优惠项目id
     * @return Page
     * @throws Exception 全局处理异常
     */
    Page pageByProjectId(Page<UnionPreferentialItem> page, Integer projectId) throws Exception;

    /**
     * 根据优惠项目id和优惠服务状态，获取优惠服务项列表信息
     *
     * @param projectId {not null} 优惠项目id
     * @param status    {not null} 优惠服务状态
     * @return List<UnionPreferentialItem>
     * @throws Exception 全局处理异常
     */
    List<UnionPreferentialItem> listByProjectIdAndStatus(Integer projectId, Integer status) throws Exception;

    /**
     * 所有过期的优惠服务，即优惠项目已不存在
     *
     * @return List<UnionPreferentialItem>
     * @throws Exception 全局处理异常
     */
    List<UnionPreferentialItem> listExpired() throws Exception;

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    /**
     * 根据商家id、盟员身份id和新增的优惠服务名称，保存新增信息
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param itemName {not null} 优惠服务名称
     * @throws Exception 全局处理异常
     */
    void saveByBusIdAndMemberIdAndName(Integer busId, Integer memberId, String itemName) throws Exception;

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    /**
     * 根据优惠服务项id列表、商家id和盟员身份id，提交优惠服务项
     *
     * @param itemIdList {not null} 优惠服务项id列表
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @throws Exception 全局处理异常
     */
    void submitBatchByIdsAndBusIdAndMemberId(List<Integer> itemIdList, Integer busId, Integer memberId) throws Exception;

    /**
     * 根据优惠服务项id列表、商家id和盟员身份id，批量移除优惠服务项
     *
     * @param itemIdList {not null} 优惠服务项id列表
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @throws Exception 全局处理异常
     */
    void removeBatchByIdsAndBusIdAndMemberId(List<Integer> itemIdList, Integer busId, Integer memberId) throws Exception;

    /**
     * 根据商家id、盟员身份id、批量操作的优惠服务项id和是否审核通过，批量审核优惠服务项
     *
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param itemIdList {not null} 优惠服务项id列表
     * @param isOK       {not null} 是否审核通过
     * @throws Exception 全局处理异常
     */
    void updateBatchByBusIdAndMemberId(Integer busId, Integer memberId, List<Integer> itemIdList, Integer isOK) throws Exception;

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    /**
     * 根据id查询对象
     *
     * @param itemId 对象id
     * @return UnionPreferentialItem
     * @throws Exception 全局处理异常
     */
    UnionPreferentialItem getById(Integer itemId) throws Exception;

    //******************************************* Object As a Service - list *******************************************

    /**
     * 根据优惠项目id查询对象列表
     *
     * @param projectId 优惠项目id
     * @return List<UnionPreferentialItem>
     * @throws Exception 全局处理异常
     */
    List<UnionPreferentialItem> listByProjectId(Integer projectId) throws Exception;

    //******************************************* Object As a Service - save *******************************************

    /**
     * 新增对象
     *
     * @param newItem 新增的对象
     * @throws Exception 全局处理异常
     */
    void save(UnionPreferentialItem newItem) throws Exception;

    /**
     * 批量新增对象
     *
     * @param newItemList 批量新增的对象列表
     * @throws Exception 全局处理异常
     */
    void saveBatch(List<UnionPreferentialItem> newItemList) throws Exception;

    //******************************************* Object As a Service - remove *****************************************

    /**
     * 根据id删除对象
     *
     * @param itemId 对象id
     * @throws Exception 全局处理异常
     */
    void removeById(Integer itemId) throws Exception;

    /**
     * 根据id列表批量删除对象
     *
     * @param itemIdList 对象id列表
     * @throws Exception 全局处理异常
     */
    void removeBatchById(List<Integer> itemIdList) throws Exception;

    //******************************************* Object As a Service - update *****************************************

    /**
     * 修改对象
     *
     * @param updateItem 修改的对象
     * @throws Exception 全局处理异常
     */
    void update(UnionPreferentialItem updateItem) throws Exception;

    /**
     * 批量修改对象
     *
     * @param updateItemList 批量修改的对象列表
     * @throws Exception 全局处理异常
     */
    void updateBatch(List<UnionPreferentialItem> updateItemList) throws Exception;
}
