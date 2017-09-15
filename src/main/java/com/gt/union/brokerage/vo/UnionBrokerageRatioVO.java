package com.gt.union.brokerage.vo;

/**
 * Created by Administrator on 2017/9/15 0015.
 */
public class UnionBrokerageRatioVO {

	private Integer toMemberId;

	private Integer fromMemberId;

	private Double fromRatio;

	private Double toRatio;

	private String name;

	public Integer getToMemberId() {
		return toMemberId;
	}

	public void setToMemberId(Integer toMemberId) {
		this.toMemberId = toMemberId;
	}

	public Integer getFromMemberId() {
		return fromMemberId;
	}

	public void setFromMemberId(Integer fromMemberId) {
		this.fromMemberId = fromMemberId;
	}

	public Double getFromRatio() {
		return fromRatio;
	}

	public void setFromRatio(Double fromRatio) {
		this.fromRatio = fromRatio;
	}

	public Double getToRatio() {
		return toRatio;
	}

	public void setToRatio(Double toRatio) {
		this.toRatio = toRatio;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
