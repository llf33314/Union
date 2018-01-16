package com.gt.union.opportunity.brokerage.service;

import com.gt.union.union.main.vo.UnionPayVO;

/**
 * @author hongjiye
 * @time 2017-12-21 10:49
 **/
public interface IUnionBrokeragePayStrategyService {

	/**
	 * 佣金支付
	 * @param orderNo        订单号
	 * @param payMoneySum    支付金额
	 * @return
	 */
	UnionPayVO unionBrokerageApply(String orderNo, Double payMoneySum);
}
