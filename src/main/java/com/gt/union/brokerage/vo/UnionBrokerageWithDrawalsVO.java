package com.gt.union.brokerage.vo;

import java.util.Date;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
public class UnionBrokerageWithDrawalsVO {

	private Date time;

	private Double money;

	private String nickName;

	private Integer memberId;

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
}
