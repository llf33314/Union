package com.gt.union.brokerage.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.brokerage.entity.UnionBrokerageIncome;

/**
 * <p>
 * 佣金收入 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionBrokerageIncomeService extends IService<UnionBrokerageIncome> {

	/**
	 * 根据商机id获取佣金收入
	 * @param id
	 * @return
	 */
	UnionBrokerageIncome getByUnionOpportunityId(Integer id);
}
