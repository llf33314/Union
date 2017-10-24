package com.gt.union.opportunity.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.opportunity.entity.UnionOpportunityRatio;

import java.util.List;

/**
 * 商机佣金比率 服务接口
 *
 * @author linweicong
 * @version 2017-10-23 11:17:59
 */
public interface IUnionOpportunityRatioService extends IService<UnionOpportunityRatio> {
    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    /**
     * 根据商机佣金比例的设置者盟员身份id和受惠者盟员身份id，获取商机佣金比例设置信息
     *
     * @param fromMemberId {not null} 设置者盟员身份id
     * @param toMemberId   {not null} 受惠者盟员身份id
     * @return UnionOpportunityRatio
     * @throws Exception 全局处理异常
     */
    UnionOpportunityRatio getByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId) throws Exception;

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    /**
     * 根据商家id和盟员身份id，分页查询商机佣金比设置列表信息
     *
     * @param page     {not null} 分页对象
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return Page
     * @throws Exception 全局处理异常
     */
    Page pageMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId) throws Exception;

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    /**
     * 根据商家id、设置方盟员身份id、受惠方盟员身份id和商机佣金比例，更新或保存设置
     *
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 设置方盟员身份id
     * @param toMemberId   {not null} 受惠方盟员身份id
     * @param ratio        {not null} 商机佣金比例
     * @throws Exception 全局处理异常
     */
    void updateOrSaveByBusIdAndFromMemberIdAndToMemberIdAndRatio(Integer busId, Integer fromMemberId, Integer toMemberId, Double ratio) throws Exception;


    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    /**
     * 根据id查询对象
     *
     * @param ratioId 对象id
     * @return UnionOpportunityRatio
     * @throws Exception 全局处理异常
     */
    UnionOpportunityRatio getById(Integer ratioId) throws Exception;

    //******************************************* Object As a Service - list *******************************************

    /**
     * 根据设置者的盟员身份id查询对象列表
     *
     * @param fromMemberId 设置者的盟员身份id
     * @return List<UnionOpportunityRatio>
     * @throws Exception 全局处理异常
     */
    List<UnionOpportunityRatio> listByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 根据目标者的盟员身份id查询对象列表
     *
     * @param toMemberId 目标者的盟员身份id
     * @return List<UnionOpportunityRatio>
     * @throws Exception 全局处理异常
     */
    List<UnionOpportunityRatio> listByToMemberId(Integer toMemberId) throws Exception;

    //******************************************* Object As a Service - save *******************************************

    /**
     * 新增对象
     *
     * @param newRatio 新增的对象
     * @throws Exception 全局处理异常
     */
    void save(UnionOpportunityRatio newRatio) throws Exception;

    /**
     * 批量新增对象
     *
     * @param newRatioList 批量新增的对象列表
     * @throws Exception 全局处理异常
     */
    void saveBatch(List<UnionOpportunityRatio> newRatioList) throws Exception;

    //******************************************* Object As a Service - remove *****************************************

    /**
     * 根据id删除对象
     *
     * @param ratioId 对象id
     * @throws Exception 全局处理异常
     */
    void removeById(Integer ratioId) throws Exception;

    /**
     * 根据id列表批量删除对象
     *
     * @param ratioIdList 对象id列表
     * @throws Exception 全局处理异常
     */
    void removeBatchById(List<Integer> ratioIdList) throws Exception;

    //******************************************* Object As a Service - update *****************************************

    /**
     * 修改对象
     *
     * @param updateRatio 修改的对象
     * @throws Exception 全局处理异常
     */
    void update(UnionOpportunityRatio updateRatio) throws Exception;

    /**
     * 批量修改对象
     *
     * @param updateRatioList 批量修改的对象列表
     * @throws Exception 全局处理异常
     */
    void updateBatch(List<UnionOpportunityRatio> updateRatioList) throws Exception;

}
