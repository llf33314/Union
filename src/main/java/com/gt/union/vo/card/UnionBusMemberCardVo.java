package com.gt.union.vo.card;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/28 0028.
 */
public class UnionBusMemberCardVo {

	/**
	 * 联盟卡号
	 */
	private String cardNo;

	/**
	 * 联盟卡类型
	 */
	private Integer cardType;

	/**
	 * 手机号
	 */
	private String phone;

	/**
	 * 开始时间
	 */
	private Date startTime;

	/**
	 * 结束时间
	 */
	private Date endTime;

	/**
	 * 联盟id
	 */
	private Integer unionId;

	/**
	 * 商家id
	 */
	private Integer busId;


	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}
}
