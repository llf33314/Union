package com.gt.union.preferential.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.preferential.entity.UnionPreferentialProject;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠项目 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionPreferentialProjectService extends IService<UnionPreferentialProject> {
    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    /**
     * 根据盟员身份id获取优惠项目
     *
     * @param memberId {not null} 盟员身份id
     * @return
     */
    UnionPreferentialProject getByMemberId(Integer memberId) throws Exception;

    /**
     * 根据商家id、盟员身份id、优惠项目id和优惠服务状态，获取详情信息
     *
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param projectId  {not null} 优惠项目id
     * @param itemStatus {not null} 优惠服务状态
     * @return
     * @throws Exception
     */
    Map<String, Object> getDetailByBusIdAndMemberIdAndProjectIdAndItemStatus(Integer busId, Integer memberId
            , Integer projectId, Integer itemStatus) throws Exception;

    /**
     * 根据商家id和盟员身份id，获取优惠项目信息
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return
     * @throws Exception
     */
    UnionPreferentialProject getByBusIdAndMemberId(Integer busId, Integer memberId) throws Exception;

    /**
     * 根据商家id和盟员身份id，获取优惠项目信息
     *
     * @param page     {not null} 分页对象
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return
     * @throws Exception
     */
    Map<String, Object> getPageMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    /**
     * 根据商家id、盟员身份id和优惠服务项状态，分页查询优惠项目列表信息
     *
     * @param page       {not null} 分页对象
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param itemStatus {not null} 优惠服务项状态
     * @return
     * @throws Exception
     */
    Page pageMapByBusIdAndMemberIdAndItemStatus(Page page, Integer busId, Integer memberId, Integer itemStatus) throws Exception;

    /**
     * 获取所有过期的优惠项目列表信息，即项目所属盟员已退盟
     *
     * @return
     * @throws Exception
     */
    List<UnionPreferentialProject> listExpired() throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

    /**
     * 根据优惠项目id、商家id和盟员身份id，更新优惠项目说明
     *
     * @param projectId    {not null} 优惠项目id
     * @param busId        {not null} 商家id
     * @param memberId     {not null} 盟员身份id
     * @param illustration {not null} 优惠项目说明
     * @throws Exception
     */
    void updateIllustrationByIdAndBusIdAndMemberId(Integer projectId, Integer busId, Integer memberId, String illustration) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - count *******************************************
     ******************************************************************************************************************/

    /**
     * 根据商家id、盟员身份id和优惠服务项状态，统计优惠服务数
     *
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param itemStatus {not null} 优惠服务状态
     * @return
     * @throws Exception
     */
    Integer countByBusInAndMemberIdAndItemStatus(Integer busId, Integer memberId, Integer itemStatus) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - boolean *****************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Object As a Service - get **********************************************
     ******************************************************************************************************************/

    UnionPreferentialProject getById(Integer projectId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    List<UnionPreferentialProject> listByMemberId(Integer memberId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    void save(UnionPreferentialProject newProject) throws Exception;

    void saveBatch(List<UnionPreferentialProject> newProjectList) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    void removeById(Integer projectId) throws Exception;

    void removeBatchById(List<Integer> projectIdList) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    void update(UnionPreferentialProject updateProject) throws Exception;

    void updateBatch(List<UnionPreferentialProject> updateProjectList) throws Exception;
}
