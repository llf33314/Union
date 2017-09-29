package com.gt.union.member.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.member.entity.UnionMemberOut;

import java.util.List;

/**
 * <p>
 * 联盟成员退盟申请 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMemberOutService extends IService<UnionMemberOut> {
    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    /**
     * 根据商家id和盟员身份id，分页获取申请退盟列表信息
     *
     * @param page     {not null} 分页对象
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return
     * @throws Exception
     */
    Page pageApplyOutMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId) throws Exception;

    /**
     * 根据商家id和盟员身份id，分页获取退盟过渡期列表信息
     *
     * @param page     {not null} 分页对象
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return
     * @throws Exception
     */
    Page pageOutingMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    /**
     * 根据商家id、盟员身份id和退盟理由，保存申请退盟信息
     *
     * @param busId          {not null} 商家id
     * @param memberId       {not null} 盟员身份id
     * @param applyOutReason {not null} 退盟理由
     * @throws Exception
     */
    void saveApplyOutByBusIdAndMemberId(Integer busId, Integer memberId, String applyOutReason) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

    /**
     * 根据商家id、盟员身份id和退盟申请id，审批退盟申请
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param outId    {not null} 退盟申请id
     * @param isOK     是否允许退盟，1为是， 0为否
     * @throws Exception
     */
    void updateByBusIdAndMemberIdAndOutId(Integer busId, Integer memberId, Integer outId, Integer isOK) throws Exception;

    /**
     * 根据商家id、盟员身份id和目标盟员身份id，直接设置目标盟员为退盟过渡期
     *
     * @param busId       {not null} 商家id
     * @param memberId    {not null} 盟员身份id
     * @param tgtMemberId {not null} 目标盟员身份id
     * @throws Exception
     */
    void updateByBusIdAndMemberIdAndTgtMemberId(Integer busId, Integer memberId, Integer tgtMemberId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - count *******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - boolean *****************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Object As a Service - get **********************************************
     ******************************************************************************************************************/

    UnionMemberOut getById(Integer outId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    List<UnionMemberOut> listByApplyMemberId(Integer applyMemberId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    void save(UnionMemberOut newOut) throws Exception;

    void saveBatch(List<UnionMemberOut> newOutList) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    void removeById(Integer outId) throws Exception;

    void removeBatchById(List<Integer> outIdList) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    void update(UnionMemberOut updateOut) throws Exception;

    void updateBatch(List<UnionMemberOut> updateOutList) throws Exception;
}
