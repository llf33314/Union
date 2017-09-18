package com.gt.union.opportunity.constant;

/**
 * Created by Administrator on 2017/9/11 0011.
 */
public interface OpportunityConstant {

	/**
	 * 受理中
	 */
	int ACCEPT_NON = 1;

	/**
	 * 已接受
	 */
	int ACCEPT_YES = 2;

	/**
	 * 已拒绝
	 */
	int ACCEPT_NO = 3;

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
	int TYPE_ONLINE = 1;

	/**
	 * 线下
	 */
	int TYPE_OFFLINE = 2;

	/**
	 * 佣金支付订单前缀
	 */
	String ORDER_PREFIX = "LMYJ";
}
