package com.gt.union.vo.business;

/**
 * Created by Administrator on 2017/7/28 0028.
 */
public class UnionBusinessRecommendVo {

	/**
	 * 所属联盟id
	 */
	private Integer unionId;

	/**
	 * 所属商家id
	 */
	private Integer busId;

	/**
	 * 商机处理状态
	 */
	private Integer isAcceptance;

	/**
	 * 商机电话号码
	 */
	private String phone;


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

	public Integer getIsAcceptance() {
		return isAcceptance;
	}

	public void setIsAcceptance(Integer isAcceptance) {
		this.isAcceptance = isAcceptance;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
