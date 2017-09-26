package com.gt.union.consume.constant;

/**
 * Created by Administrator on 2017/9/13 0013.
 */
public interface ConsumeConstant {

	/**
	 * 未支付
	 */
	int PAY_STATUS_NON = 1;

	/**
	 * 已支付
	 */
	int PAY_STATUS_YES = 2;

	/**
	 * 已退款
	 */
	int PAY_STATUS_REFUND = 3;

	/**
	 * 联盟核销订单号前缀
	 */
	String ORDER_PREFIX = "LMHX";
}
