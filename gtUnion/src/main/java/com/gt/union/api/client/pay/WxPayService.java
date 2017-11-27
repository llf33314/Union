package com.gt.union.api.client.pay;


import com.gt.union.common.response.GtJsonResult;

import java.util.Map;

/**
 * 微信支付服务
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
public interface WxPayService {

	/**
	 * 二维码支付
	 * @param totalFee		支付金额
	 * @param appidType		appid类型
	 * @param orderNum		支付订单号
	 * @param desc			支付订单描述
	 * @param isreturn		支付成功是否同步跳转（1：是 0：否） 如果为1，returnUrl必填
	 * @param returnUrl		同步跳转地址
	 * @param notifyUrl		异步回调url，使用post请求方式。会传out_trade_no—订单号，payType—支付类型（0：微信1：支付宝2：多粉钱包）。处理完业务后必须返回回调结果：code(0:成功-1:失败)，msg(处理结果描述，如：成功)
	 * @param isSendMessage	是否需要消息推送 1：需要（sendUrl必传），0：不需要（sendUrl可以不传）
	 * @param sendUrl		消息推送地址（不能带参数）
	 * @param payWay		支付方式 0：系统判断 1：微信 2：支付宝 3：多粉钱包
	 * @param extend		扩展属性
	 * @return
	 */
	String qrCodePay(Double totalFee, Integer appidType, String orderNum, String desc, Integer isreturn, String returnUrl, String notifyUrl, Integer isSendMessage,
							String sendUrl, Integer payWay, Map extend);


	/**
	 * 手机端支付，返回支付页面链接
	 * @param totalFee		支付金额
	 * @param appidType		appid类型
	 * @param orderNum		支付订单号
	 * @param desc			支付订单描述
	 * @param isreturn		支付成功是否同步跳转（1：是 0：否） 如果为1，returnUrl必填
	 * @param returnUrl		同步跳转地址
	 * @param notifyUrl		异步回调url，使用post请求方式。会传out_trade_no—订单号，payType—支付类型（0：微信1：支付宝2：多粉钱包）。处理完业务后必须返回回调结果：code(0:成功-1:失败)，msg(处理结果描述，如：成功)
	 * @param isSendMessage	是否需要消息推送 1：需要（sendUrl必传），0：不需要（sendUrl可以不传）
	 * @param sendUrl		消息推送地址（不能带参数）
	 * @param payWay		支付方式 0：系统判断 1：微信 2：支付宝 3：多粉钱包
	 * @param extend		扩展属性
	 * @return
	 */
	String pay(Double totalFee, Integer appidType, String orderNum, String desc, Integer isreturn, String returnUrl, String notifyUrl, Integer isSendMessage,
					  String sendUrl, Integer payWay, Map extend);

	/**
	 * 商家提现
	 * @param partnerTradeNo		系统订单号
	 * @param openid				提现人openid（该值从session的member里面获取）
	 * @param desc					提现描述
	 * @param amount				提现金额
	 * @param paySource				来源 0—公众号 1—小程序 默认：0
	 * @return
	 */
	GtJsonResult enterprisePayment(String partnerTradeNo, String openid, String desc, Double amount, Integer paySource);
}
