package com.gt.union.entity.common;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/26 0026.
 */
public class UnionAuthorityInfo {

	/**
	 * 商家账号
	 */
	private Integer busId;

	/**
	 * 有效期
	 */
	private Date date;

	/**
	 * 金额
	 */
	private Double price;

	/**
	 * 盟员数
	 */
	private Integer memberNum;

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getMemberNum() {
		return memberNum;
	}

	public void setMemberNum(Integer memberNum) {
		this.memberNum = memberNum;
	}
}
