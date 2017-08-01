package com.gt.union.vo.business;

import com.gt.union.entity.business.UnionBusinessRecommendInfo;

/**
 * 保存数据的vo
 * Created by Administrator on 2017/7/28 0028.
 */
public class UnionBusinessRecommendFormVo {

	/**
	 * 商家id
	 */
	private Integer busId;

	/**
	 * 联盟id
	 */
	private Integer unionId;

	/**
	 * 推荐的盟员id
	 */
	private Integer toMemberId;

	/**
	 * 被推荐的商家id
	 */
	private Integer toBusId;

	/**
	 * 推荐信息
	 */
	private UnionBusinessRecommendInfo unionBusinessRecommendInfo;


	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Integer getToMemberId() {
		return toMemberId;
	}

	public void setToMemberId(Integer toMemberId) {
		this.toMemberId = toMemberId;
	}

	public Integer getToBusId() {
		return toBusId;
	}

	public void setToBusId(Integer toBusId) {
		this.toBusId = toBusId;
	}

	public UnionBusinessRecommendInfo getUnionBusinessRecommendInfo() {
		return unionBusinessRecommendInfo;
	}

	public void setUnionBusinessRecommendInfo(UnionBusinessRecommendInfo unionBusinessRecommendInfo) {
		this.unionBusinessRecommendInfo = unionBusinessRecommendInfo;
	}
}
