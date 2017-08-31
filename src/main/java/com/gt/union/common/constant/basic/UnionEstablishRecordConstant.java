package com.gt.union.common.constant.basic;

/**
 * Created by Administrator on 2017/8/31 0031.
 */
public interface UnionEstablishRecordConstant {

	/**
	 * 创建联盟的状态
	 */
	int CREATE_UNION_STATUS_YES = 1;

	/**
	 * 为创建联盟状态
	 */
	int CREATE_UNION_STATUS_NO = 0;

	/**
	 * 订单状态：未支付
	 */
	int ORDER_UNPAY = 1;

	/**
	 * 订单状态：支付成功
	 */
	int ORDER_PAY_SUCCESS = 2;

	/**
	 * 订单状态：支付失败
	 */
	int ORDER_PAY_FAIL = 3;

	/**
	 * 支付方式：微信
	 */
	int PAY_WAY_WX = 1;

	/**
	 * 支付方式：粉币
	 */
	int PAY_WAY_FENBI = 2;

	/**
	 * 支付方式：支付宝
	 */
	int PAY_WAY_ALI = 3;

	/**
	 * 支付类型：创建联盟
	 */
	int PAY_TYPE_CREATE_UNION = 1;

}
