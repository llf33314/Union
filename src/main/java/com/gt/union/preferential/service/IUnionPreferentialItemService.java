package com.gt.union.preferential.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.preferential.entity.UnionPreferentialItem;

import java.util.List;

/**
 * <p>
 * 优惠服务项 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionPreferentialItemService extends IService<UnionPreferentialItem> {
    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    /**
     * 根据优惠项目id，分页获取优惠服务列表信息
     *
     * @param page      {not null} 分页对象
     * @param projectId {not null} 优惠项目id
     * @return
     * @throws Exception
     */
    Page pageByProjectId(Page page, Integer projectId) throws Exception;

    /**
     * 根据优惠项目id和优惠服务状态，获取优惠服务项列表信息
     *
     * @param projectId {not null} 优惠项目id
     * @param status    {not null} 优惠服务状态
     * @return
     * @throws Exception
     */
    List<UnionPreferentialItem> listByProjectIdAndStatus(Integer projectId, Integer status) throws Exception;

    /**
     * 所有过期的优惠服务，即优惠项目已不存在
     *
     * @return
     * @throws Exception
     */
    List<UnionPreferentialItem> listExpired() throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    /**
     * 根据商家id、盟员身份id和新增的优惠服务名称，保存新增信息
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param itemName {not null} 优惠服务名称
     * @throws Exception
     */
    void saveByBusIdAndMemberIdAndName(Integer busId, Integer memberId, String itemName) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

    /**
     * 根据优惠服务项id列表、商家id和盟员身份id，提交优惠服务项
     *
     * @param itemIdList {not null} 优惠服务项id列表
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @throws Exception
     */
    void submitBatchByIdsAndBusIdAndMemberId(List<Integer> itemIdList, Integer busId, Integer memberId) throws Exception;

    /**
     * 根据优惠服务项id列表、商家id和盟员身份id，批量移除优惠服务项
     *
     * @param itemIdList {not null} 优惠服务项id列表
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @throws Exception
     */
    void removeBatchByIdsAndBusIdAndMemberId(List<Integer> itemIdList, Integer busId, Integer memberId) throws Exception;

    /**
     * 根据商家id、盟员身份id、批量操作的优惠服务项id和是否审核通过，批量审核优惠服务项
     *
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param itemIdList {not null} 优惠服务项id列表
     * @param isOK       {not null} 是否审核通过
     * @throws Exception
     */
    void updateBatchByBusIdAndMemberId(Integer busId, Integer memberId, List<Integer> itemIdList, Integer isOK) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - count *******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - boolean *****************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Object As a Service - get **********************************************
     ******************************************************************************************************************/

    UnionPreferentialItem getById(Integer itemId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    List<UnionPreferentialItem> listByProjectId(Integer projectId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    void save(UnionPreferentialItem newItem) throws Exception;

    void saveBatch(List<UnionPreferentialItem> newItemList) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    void removeById(Integer itemId) throws Exception;

    void removeBatchById(List<Integer> itemIdList) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    void update(UnionPreferentialItem updateItem) throws Exception;

    void updateBatch(List<UnionPreferentialItem> updateItemList) throws Exception;
}
