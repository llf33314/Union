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

    /**
     * 根据商机佣金比例的设置者盟员身份id和受惠者盟员身份id，获取商机佣金比例设置信息
     *
     * @param fromMemberId {not null} 设置者盟员身份id
     * @param toMemberId   {not null} 受惠者盟员身份id
     * @return
     * @throws Exception
     */
    UnionOpportunityBrokerageRatio getByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId) throws Exception;

    /**
     * 根据商家id查询佣金比列表
     *
     * @param page
     * @param busId   商家id
     * @param unionId 联盟id
     * @return
     */
    Page pageBrokerageRatio(Page page, Integer busId, Integer unionId) throws Exception;

    /**
     * 设置商机佣金比
     *
     * @param fromMemberId 设置的盟员id
     * @param busId        商家id
     * @param toMemberId   被设置的盟员id
     * @param ratio
     * @return
     */
    void saveOrUpdateBrokerageRatio(Integer fromMemberId, Integer busId, Integer toMemberId, Double ratio) throws Exception;
}
