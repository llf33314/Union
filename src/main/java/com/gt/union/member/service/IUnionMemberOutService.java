package com.gt.union.member.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.member.entity.UnionMemberOut;

/**
 * <p>
 * 联盟成员退盟申请 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMemberOutService extends IService<UnionMemberOut> {
    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据退盟申请id，获取退盟申请信息
     *
     * @param outId {not null} 退盟申请id
     * @return
     * @throws Exception
     */
    UnionMemberOut getById(Integer outId) throws Exception;

    /**
     * 根据退盟申请的盟员身份id，获取退盟申请信息
     *
     * @param applyMemberId {not null} 退盟申请id
     * @return
     * @throws Exception
     */
    UnionMemberOut getByApplyMemberId(Integer applyMemberId) throws Exception;

    //------------------------------------------ list(include page) ---------------------------------------------------

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

    //------------------------------------------------- update --------------------------------------------------------

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

    //------------------------------------------------- save ----------------------------------------------------------

    /**
     * 根据商家id、盟员身份id和退盟理由，保存申请退盟信息
     *
     * @param busId          {not null} 商家id
     * @param memberId       {not null} 盟员身份id
     * @param applyOutReason {not null} 退盟理由
     * @throws Exception
     */
    void saveApplyOutByBusIdAndMemberId(Integer busId, Integer memberId, String applyOutReason) throws Exception;

    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------
}
