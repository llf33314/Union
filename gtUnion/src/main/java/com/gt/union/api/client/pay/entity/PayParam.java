package com.gt.union.api.client.pay.entity;

import java.util.Map;

/**
 * 支付参数
 * @author hongjiye
 * @time 2017-12-11 17:10
 **/
public class PayParam {

	/**
	 * 支付金额
	 */
	private Double totalFee;

	/**
	 * appid类型
	 */
	private Integer appidType;

	/**
	 * 支付订单号
	 */
	private String orderNum;

	/**
	 * 支付订单描述
	 */
	private String desc;

	/**
	 * 支付成功是否同步跳转（1：是 0：否） 如果为1，returnUrl必填
	 */
	private Integer isreturn;

	/**
	 * 同步跳转地址
	 */
	private String returnUrl;

	/**
	 * 异步回调url，使用post请求方式。会传out_trade_no—订单号，payType—支付类型（0：微信1：支付宝2：多粉钱包）。处理完业务后必须返回回调结果：code(0:成功-1:失败)，msg(处理结果描述，如：成功)
	 */
	private String notifyUrl;

	/**
	 * 是否需要消息推送 1：需要（sendUrl必传），0：不需要（sendUrl可以不传）
	 */
	private Integer isSendMessage;

	/**
	 * 消息推送地址（不能带参数）
	 */
	private String sendUrl;

	/**
	 * 支付方式 0：系统判断 1：微信 2：支付宝 3：多粉钱包
	 */
	private Integer payWay;

	/**
	 * 扩展属性
	 */
	private Map extend;


	public Double getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(Double totalFee) {
		this.totalFee = totalFee;
	}

	public Integer getAppidType() {
		return appidType;
	}

	public void setAppidType(Integer appidType) {
		this.appidType = appidType;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Integer getIsreturn() {
		return isreturn;
	}

	public void setIsreturn(Integer isreturn) {
		this.isreturn = isreturn;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public Integer getIsSendMessage() {
		return isSendMessage;
	}

	public void setIsSendMessage(Integer isSendMessage) {
		this.isSendMessage = isSendMessage;
	}

	public String getSendUrl() {
		return sendUrl;
	}

	public void setSendUrl(String sendUrl) {
		this.sendUrl = sendUrl;
	}

	public Integer getPayWay() {
		return payWay;
	}

	public void setPayWay(Integer payWay) {
		this.payWay = payWay;
	}

	public Map getExtend() {
		return extend;
	}

	public void setExtend(Map extend) {
		this.extend = extend;
	}
}
