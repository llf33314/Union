package com.gt.union.h5.card.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author hongjiye
 * @time 2017-12-20 17:45
 **/
@ApiModel(value = "我的联盟卡列表详情")
public class MyUnionCardDetailVO {

	@ApiModelProperty(value = "联盟卡名称")
	private String cardName;

	@ApiModelProperty(value = "联盟卡类型 1:折扣卡 2:活动卡")
	private Integer cardType;

	@ApiModelProperty(value = "联盟id")
	private Integer unionId;

	@ApiModelProperty(value = "联盟活动卡id")
	private Integer activityId;

	@ApiModelProperty(value = "联盟活动卡颜色1")
	private String color1;

	@ApiModelProperty(value = "联盟活动卡颜色2")
	private String color2;

	@ApiModelProperty(value = "活动卡优惠项目数")
	private Integer itemCount;

	@ApiModelProperty(value = "联盟活动卡有效期字符串")
	private String validityStr;

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public String getColor1() {
		return color1;
	}

	public void setColor1(String color1) {
		this.color1 = color1;
	}

	public String getColor2() {
		return color2;
	}

	public void setColor2(String color2) {
		this.color2 = color2;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	public String getValidityStr() {
		return validityStr;
	}

	public void setValidityStr(String validityStr) {
		this.validityStr = validityStr;
	}
}
