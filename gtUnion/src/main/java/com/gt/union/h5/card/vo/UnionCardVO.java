package com.gt.union.h5.card.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author hongjiye
 * @time 2017-12-20 16:28
 **/
@ApiModel(value = "h5联盟卡首页列表详情VO")
public class UnionCardVO {

	@ApiModelProperty(value = "联盟卡类型 1:折扣卡 2:活动卡")
	private Integer cardType;

	@ApiModelProperty(value = "联盟卡名称")
	private String cardName;

	@ApiModelProperty(value = "联盟活动卡颜色")
	private String color;

	@ApiModelProperty(value = "活动卡id")
	private Integer activityId;

	@ApiModelProperty(value = "联盟id")
	private Integer unionId;

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}
}
