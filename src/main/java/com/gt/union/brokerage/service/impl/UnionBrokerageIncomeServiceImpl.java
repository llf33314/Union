package com.gt.union.brokerage.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.brokerage.mapper.UnionBrokerageIncomeMapper;
import com.gt.union.brokerage.service.IUnionBrokerageIncomeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 佣金收入 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionBrokerageIncomeServiceImpl extends ServiceImpl<UnionBrokerageIncomeMapper, UnionBrokerageIncome> implements IUnionBrokerageIncomeService {

	@Override
	public UnionBrokerageIncome getByUnionOpportunityId(Integer id) {
		EntityWrapper wrapper = new EntityWrapper();
		wrapper.eq("opportunity_id", id);
		return this.selectOne(wrapper);
	}
}
