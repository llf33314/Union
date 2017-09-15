package com.gt.union.opportunity.constant;

/**
 * Created by Administrator on 2017/9/11 0011.
 */
public interface OpportunityConstant {

	/**
	 * 未处理
	 */
	int ACCEPT_CONFIRM_NON = 1;

	/**
	 * 已处理
	 */
	int ACCEPT_CONFIRM_YES = 2;

	/**
	 * 已拒绝
	 */
	int ACCEPT_CONFIRM_NO = 3;

	/**
	 * 未支付
	 */
	int BROKERAGE_PAY_NO = 1;

	/**
	 * 已支付
	 */
	int BROKERAGE_PAY_YES = 2;

	/**
	 * 线上
	 */
	int OPPORTUNITY_TYPE_ONLINE = 1;

	/**
	 * 线下
	 */
	int OPPORTUNITY_TYPE_OFFLINE = 2;

	/**
	 * 佣金支付订单前缀
	 */
	String ORDER_PREFIX = "LMYJ";
}
