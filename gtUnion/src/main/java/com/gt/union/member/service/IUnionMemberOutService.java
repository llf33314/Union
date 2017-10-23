package com.gt.union.member.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.member.entity.UnionMemberOut;

import java.util.List;

/**
 * 联盟成员退盟申请 服务接口
 *
 * @author linweicong
 * @version 2017-10-23 08:34:54
 */
public interface IUnionMemberOutService extends IService<UnionMemberOut> {
    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    /**
     * 根据商家id和盟员身份id，分页获取申请退盟列表信息
     *
     * @param page     {not null} 分页对象
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return Page
     * @throws Exception 全局处理异常
     */
    Page pageApplyOutMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId) throws Exception;

    /**
     * 根据商家id和盟员身份id，分页获取退盟过渡期列表信息
     *
     * @param page     {not null} 分页对象
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return Page
     * @throws Exception 全局处理异常
     */
    Page pageOutingMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId) throws Exception;

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    /**
     * 根据商家id、盟员身份id和退盟理由，保存申请退盟信息
     *
     * @param busId          {not null} 商家id
     * @param memberId       {not null} 盟员身份id
     * @param applyOutReason {not null} 退盟理由
     * @throws Exception 全局处理异常
     */
    void saveApplyOutByBusIdAndMemberId(Integer busId, Integer memberId, String applyOutReason) throws Exception;

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    /**
     * 根据商家id、盟员身份id和退盟申请id，审批退盟申请
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param outId    {not null} 退盟申请id
     * @param isOK     是否允许退盟，1为是， 0为否
     * @throws Exception 全局处理异常
     */
    void updateByBusIdAndMemberIdAndOutId(Integer busId, Integer memberId, Integer outId, Integer isOK) throws Exception;

    /**
     * 根据商家id、盟员身份id和目标盟员身份id，直接设置目标盟员为退盟过渡期
     *
     * @param busId       {not null} 商家id
     * @param memberId    {not null} 盟员身份id
     * @param tgtMemberId {not null} 目标盟员身份id
     * @throws Exception 全局处理异常
     */
    void updateByBusIdAndMemberIdAndTgtMemberId(Integer busId, Integer memberId, Integer tgtMemberId) throws Exception;

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    /**
     * 根据id查询对象
     *
     * @param outId 对象id
     * @return UnionMemberOut
     * @throws Exception 全局处理异常
     */
    UnionMemberOut getById(Integer outId) throws Exception;

    //******************************************* Object As a Service - list *******************************************

    /**
     * 根据退盟申请人的盟员身份id查询对象列表
     *
     * @param applyMemberId 退盟申请人的盟员身份id
     * @return List<UnionMemberOut>
     * @throws Exception 全局处理异常
     */
    List<UnionMemberOut> listByApplyMemberId(Integer applyMemberId) throws Exception;

    //******************************************* Object As a Service - save *******************************************

    /**
     * 新增对象
     *
     * @param newOut 新增的对象
     * @throws Exception 全局处理异常
     */
    void save(UnionMemberOut newOut) throws Exception;

    /**
     * 批量新增对象
     *
     * @param newOutList 批量新增的对象列表
     * @throws Exception 全局处理异常
     */
    void saveBatch(List<UnionMemberOut> newOutList) throws Exception;

    //******************************************* Object As a Service - remove *****************************************

    /**
     * 根据id删除对象
     *
     * @param outId 对象id
     * @throws Exception 全局处理异常
     */
    void removeById(Integer outId) throws Exception;

    /**
     * 根据id列表批量删除对象
     *
     * @param outIdList 对象id列表
     * @throws Exception 全局处理异常
     */
    void removeBatchById(List<Integer> outIdList) throws Exception;

    //******************************************* Object As a Service - update *****************************************

    /**
     * 修改对象
     *
     * @param updateOut 修改的对象
     * @throws Exception 全局处理异常
     */
    void update(UnionMemberOut updateOut) throws Exception;

    /**
     * 批量修改对象
     *
     * @param updateOutList 批量修改的对象列表
     * @throws Exception 全局处理异常
     */
    void updateBatch(List<UnionMemberOut> updateOutList) throws Exception;
}
