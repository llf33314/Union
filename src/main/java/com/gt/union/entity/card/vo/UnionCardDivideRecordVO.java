package com.gt.union.entity.card.vo;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/31 0031.
 */
public class UnionCardDivideRecordVO {

	/**
	 * 联盟id
	 */
	private Integer unionId;

	/**
	 * 联名卡类型 1：黑卡 2：红卡
	 */
	private Integer cardType;

	/**
	 * 联盟卡号
	 */
	private String cardNo;

	/**
	 * 起始日期
	 */
	private Date startTime;

	/**
	 * 结束日期
	 */
	private Date endTime;

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
