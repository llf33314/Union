package com.gt.union.brokerage.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.brokerage.entity.UnionBrokerageRatio;

/**
 * <p>
 * 商机佣金比率 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionBrokerageRatioService extends IService<UnionBrokerageRatio> {

	/**
	 * 查询佣金设置比例
	 * @param fromMemberId
	 * @param toMemberId
	 * @return
	 */
	UnionBrokerageRatio getByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId) throws Exception;

	/**
	 * 根据商家id查询佣金比列表
	 * @param page
	 * @param busId		商家id
	 * @param unionId	联盟id
	 * @return
	 */
	Page pageBrokerageRatio(Page page, Integer busId, Integer unionId) throws Exception;

	/**
	 * 设置商机佣金比
	 * @param fromMemberId    设置的盟员id
	 * @param busId            商家id
	 * @param toMemberId    被设置的盟员id
	 * @param ratio
	 * @return
	 */
	void saveOrUpdateBrokerageRatio(Integer fromMemberId, Integer busId, Integer toMemberId, Double ratio) throws Exception;
}
