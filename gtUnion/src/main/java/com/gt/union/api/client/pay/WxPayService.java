package com.gt.union.api.client.pay;


import com.gt.union.api.client.pay.entity.PayParam;
import com.gt.union.common.response.GtJsonResult;

import java.util.Map;

/**
 * 微信支付服务
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
public interface WxPayService {

	/**
	 * 二维码支付  返回二维码链接
	 * @param payParam
	 * @return
	 */
	String qrCodePay(PayParam payParam);


	/**
	 * 手机端支付，返回支付页面链接
	 * @param payParam  支付参数
	 * @return
	 */
	String pay(PayParam payParam);

	/**
	 * 微信小程序支付，返回支付链接
	 * @param payParam	支付参数
	 * @return
	 */
	String wxAppPay(PayParam payParam);


	/**
	 * 商家提现
	 * @param partnerTradeNo		系统订单号
	 * @param openid				提现人openid（该值从session的member里面获取）
	 * @param desc					提现描述
	 * @param amount				提现金额
	 * @param paySource				来源 0—公众号 1—小程序 默认：0
	 * @return
	 */
	GtJsonResult enterprisePayment(String partnerTradeNo, String openid, String desc, Double amount, Integer paySource) throws Exception;

	/**
	 * 退款
	 * @param sysOrderNo	支付订单号
	 * @param refundFee		退款金额
	 * @param totalFee		总金额
	 * @return
	 */
	GtJsonResult refundOrder(String sysOrderNo, Double refundFee, Double totalFee);
}
