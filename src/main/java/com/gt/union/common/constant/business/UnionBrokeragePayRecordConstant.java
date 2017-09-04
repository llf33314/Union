package com.gt.union.common.constant.business;

/**
 * Created by Administrator on 2017/9/4 0004.
 */
public interface UnionBrokeragePayRecordConstant {

	String orderPrefix = "LMYJ";

	/**
	 * 删除
	 */
	int DEL_STATUS_NO = 0;

	/**
	 * 未删除
	 */
	int DEL_STATUS_YES = 1;

	/**
	 * 支付成功
	 */
	int PAY_STATUS_SUCCESS = 1;

	/**
	 * 微信支付
	 */
	int PAY_TYPE_WX = 1;
}
