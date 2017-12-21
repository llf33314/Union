package com.gt.union.h5.card.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * @author hongjiye
 * @time 2017-12-19 15:23
 **/
@ApiModel(value = "联盟卡详情")
public class CardDetailVO {

	@ApiModelProperty(value = "是否可以办理联盟卡 0：否 1：是")
	private Integer isTransacted;

	@ApiModelProperty(value = "联盟卡类型 1:折扣卡 2:活动卡")
	private Integer cardType;

	@ApiModelProperty(value = "联盟卡名称")
	private String cardName;

	@ApiModelProperty(value = "联盟活动卡颜色")
	private String color;

	@ApiModelProperty(value = "联盟活动卡有效期字符串")
	private String validityStr;

	@ApiModelProperty(value = "联盟活动卡是否过期 0：未过期  1：已过期")
	private Integer isOverdue;

	@ApiModelProperty(value = "折扣卡商家数")
	private Integer userCount;

	@ApiModelProperty(value = "活动卡优惠项目数")
	private Integer itemCount;

	@ApiModelProperty(value = "活动卡价格")
	private Double cardPrice;

	@ApiModelProperty(value = "联盟卡详情页-列表信息")
	private List<CardDetailListVO> cardDetailListVO;

	public Integer getIsTransacted() {
		return isTransacted;
	}

	public void setIsTransacted(Integer isTransacted) {
		this.isTransacted = isTransacted;
	}

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

	public String getValidityStr() {
		return validityStr;
	}

	public void setValidityStr(String validityStr) {
		this.validityStr = validityStr;
	}

	public Integer getIsOverdue() {
		return isOverdue;
	}

	public void setIsOverdue(Integer isOverdue) {
		this.isOverdue = isOverdue;
	}

	public Integer getUserCount() {
		return userCount;
	}

	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	public Double getCardPrice() {
		return cardPrice;
	}

	public void setCardPrice(Double cardPrice) {
		this.cardPrice = cardPrice;
	}

	public List<CardDetailListVO> getCardDetailListVO() {
		return cardDetailListVO;
	}

	public void setCardDetailListVO(List<CardDetailListVO> cardDetailListVO) {
		this.cardDetailListVO = cardDetailListVO;
	}
}
