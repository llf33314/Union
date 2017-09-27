package com.gt.union.opportunity.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.opportunity.entity.UnionOpportunityBrokerageRatio;

/**
 * <p>
 * 商机佣金比率 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionOpportunityBrokerageRatioService extends IService<UnionOpportunityBrokerageRatio> {
    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据商机佣金比例的设置者盟员身份id和受惠者盟员身份id，获取商机佣金比例设置信息
     *
     * @param fromMemberId {not null} 设置者盟员身份id
     * @param toMemberId   {not null} 受惠者盟员身份id
     * @return
     * @throws Exception
     */
    UnionOpportunityBrokerageRatio getByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId) throws Exception;

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 根据商家id和盟员身份id，分页查询商机佣金比设置列表信息
     *
     * @param page     {not null} 分页对象
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return
     * @throws Exception
     */
    Page pageMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId) throws Exception;

    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据商家id、设置方盟员身份id、受惠方盟员身份id和商机佣金比例，更新或保存设置
     *
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 设置方盟员身份id
     * @param toMemberId   {not null} 受惠方盟员身份id
     * @param ratio        {not null} 商机佣金比例
     * @throws Exception
     */
    void updateOrSaveByBusIdAndFromMemberIdAndToMemberIdAndRatio(Integer busId, Integer fromMemberId, Integer toMemberId, Double ratio) throws Exception;

    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------

}
