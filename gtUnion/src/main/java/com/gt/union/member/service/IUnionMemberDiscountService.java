package com.gt.union.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.member.entity.UnionMemberDiscount;

import java.util.List;

/**
 * 盟员折扣 服务接口
 *
 * @author linweicong
 * @version 2017-10-23 08:34:54
 */
public interface IUnionMemberDiscountService extends IService<UnionMemberDiscount> {
    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    /**
     * 根据设置折扣盟员id和受惠折扣盟员id，获取折扣信息
     *
     * @param fromMemberId {not null} 设置折扣盟员id
     * @param toMemberId   {not null} 受惠折扣盟员id
     * @return List<UnionMemberDiscount>
     * @throws Exception 全局处理异常
     */
    List<UnionMemberDiscount> listByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId) throws Exception;

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    /**
     * 根据商家id、商家盟员身份id、被设置折扣的盟员身份id以及设置的折扣信息，更新或保存折扣信息
     *
     * @param busId       {not null} 商家id
     * @param memberId    {not null} 操作人的盟员身份id
     * @param tgtMemberId {not null} 被设置的折扣信息
     * @param discount    {not null} 折扣
     * @throws Exception 全局处理异常
     */
    void updateOrSaveDiscountByBusIdAndMemberId(Integer busId, Integer memberId, Integer tgtMemberId, Double discount) throws Exception;

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    /**
     * 根据id查询对象
     *
     * @param discountId 对象id
     * @return UnionMemberDiscount
     * @throws Exception 全局处理异常
     */
    UnionMemberDiscount getById(Integer discountId) throws Exception;

    //******************************************* Object As a Service - list *******************************************

    /**
     * 根据折扣设置者盟员身份id查询对象列表
     *
     * @param fromMemberId 折扣设置者盟员身份id
     * @return List<UnionMemberDiscount>
     * @throws Exception 全局处理异常
     */
    List<UnionMemberDiscount> listByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 根据折扣受惠者盟员身份id查询对象列表
     *
     * @param toMemberId 折扣受惠者盟员身份id
     * @return List<UnionMemberDiscount>
     * @throws Exception 全局处理异常
     */
    List<UnionMemberDiscount> listByToMemberId(Integer toMemberId) throws Exception;

    //******************************************* Object As a Service - save *******************************************

    /**
     * 新增对象
     *
     * @param newDiscount 新增的对象
     * @throws Exception 全局处理异常
     */
    void save(UnionMemberDiscount newDiscount) throws Exception;

    /**
     * 批量新增对象
     *
     * @param newDiscountList 批量新增的对象列表
     * @throws Exception 全局处理异常
     */
    void saveBatch(List<UnionMemberDiscount> newDiscountList) throws Exception;

    //******************************************* Object As a Service - remove *****************************************

    /**
     * 根据id删除对象
     *
     * @param discountId 对象id
     * @throws Exception 全局处理异常
     */
    void removeById(Integer discountId) throws Exception;

    /**
     * 根据id列表批量删除对象
     *
     * @param discountIdList 对象id列表
     * @throws Exception 全局处理异常
     */
    void removeBatchById(List<Integer> discountIdList) throws Exception;

    //******************************************* Object As a Service - update *****************************************

    /**
     * 修改对象
     *
     * @param updateDiscount 修改的对象
     * @throws Exception 全局处理异常
     */
    void update(UnionMemberDiscount updateDiscount) throws Exception;

    /**
     * 批量修改对象
     *
     * @param updateDiscountList 批量修改的对象列表
     * @throws Exception 全局处理异常
     */
    void updateBatch(List<UnionMemberDiscount> updateDiscountList) throws Exception;

}
