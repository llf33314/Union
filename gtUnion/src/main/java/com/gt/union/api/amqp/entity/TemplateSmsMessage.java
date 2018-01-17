package com.gt.union.api.amqp.entity;

import com.alibaba.fastjson.JSON;

/**
 * @author hongjiye
 * @time 2018-01-12 16:32
 **/
public class TemplateSmsMessage extends SmsMessage{

	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 短信内容  多变量用逗号隔开
	 */
	private String paramsStr;

	/**
	 * 商家id
	 */
	private Integer busId;

	/**
	 * 短信模块ID
	 */
	private Integer model;

	/**
	 * 短信模板id
	 */
	private Long tmplId;

	/**
	 * 国家 不填为86
	 */
	private String country;

	public TemplateSmsMessage() {
		this.country = "86";
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getParamsStr() {
		return paramsStr;
	}

	public void setParamsStr(String paramsStr) {
		this.paramsStr = paramsStr;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public Integer getModel() {
		return model;
	}

	public void setModel(Integer model) {
		this.model = model;
	}

	public Long getTmplId() {
		return tmplId;
	}

	public void setTmplId(Long tmplId) {
		this.tmplId = tmplId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
