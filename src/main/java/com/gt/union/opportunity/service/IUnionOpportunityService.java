package com.gt.union.opportunity.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.opportunity.entity.UnionOpportunity;

/**
 * <p>
 * 商机推荐 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionOpportunityService extends IService<UnionOpportunity> {

	/**
	 * 查询推荐给我的商机
	 * @param page
	 * @param busId		商家id
	 * @param unionId	联盟id
	 * @param isAccept	处理状态
	 * @param clientName	客户名称
	 * @param clientPhone	客户电话
	 * @return
	 */
	Page listToMy(Page page, Integer busId, Integer unionId, String isAccept, String clientName, String clientPhone) throws Exception;

	/**
	 * 查询我推荐的商机
	 * @param page
	 * @param busId		商家id
	 * @param unionId	联盟id
	 * @param isAccept	处理状态
	 * @param clientName	客户名称
	 * @param clientPhone	客户电话
	 * @return
	 */
	Page listFromMy(Page page, Integer busId, Integer unionId, String isAccept, String clientName, String clientPhone) throws Exception;

	/**
	 * 审核商机
	 * @param busId	商家id
	 * @param id	商机id
	 * @param isAccept	处理状态
	 * @param acceptPrice	受理价格
	 */
	void updateByIdAndIsAccept(Integer busId, Integer id, Integer isAccept, Double acceptPrice);
}
