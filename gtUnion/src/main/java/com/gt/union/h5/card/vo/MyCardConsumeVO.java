package com.gt.union.h5.card.vo;

import com.gt.union.card.project.entity.UnionCardProjectItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author hongjiye
 * @time 2017-12-21 11:44
 **/
@ApiModel(value = "联盟卡-消费记录")
public class MyCardConsumeVO {

	@ApiModelProperty(value = "消费记录id")
	private Integer recordId;

	@ApiModelProperty(value = "门店名称")
	private String shopName;

	@ApiModelProperty(value = "消费金额")
	private Double consumeMoney;

	@ApiModelProperty(value = "支付金额")
	private Double payMoney;

	@ApiModelProperty(value = "折扣金额")
	private Double DiscountMoney;

	@ApiModelProperty(value = "折扣")
	private Double discount;

	@ApiModelProperty(value = "消费积分")
	private Double consumeIntegral;

	@ApiModelProperty(value = "积分抵扣金额")
	private Double integralMoney;

	@ApiModelProperty(value = "支付方式 0:现金 1:微信 2:支付宝")
	private Integer payType;

	@ApiModelProperty(value = "赠送积分")
	private Double giveIntegral;

	@ApiModelProperty(value = "优惠项目")
	private List<UnionCardProjectItem> items;

	public Integer getRecordId() {
		return recordId;
	}

	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public Double getConsumeMoney() {
		return consumeMoney;
	}

	public void setConsumeMoney(Double consumeMoney) {
		this.consumeMoney = consumeMoney;
	}

	public Double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}

	public Double getDiscountMoney() {
		return DiscountMoney;
	}

	public void setDiscountMoney(Double discountMoney) {
		DiscountMoney = discountMoney;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getConsumeIntegral() {
		return consumeIntegral;
	}

	public void setConsumeIntegral(Double consumeIntegral) {
		this.consumeIntegral = consumeIntegral;
	}

	public Double getIntegralMoney() {
		return integralMoney;
	}

	public void setIntegralMoney(Double integralMoney) {
		this.integralMoney = integralMoney;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Double getGiveIntegral() {
		return giveIntegral;
	}

	public void setGiveIntegral(Double giveIntegral) {
		this.giveIntegral = giveIntegral;
	}

	public List<UnionCardProjectItem> getItems() {
		return items;
	}

	public void setItems(List<UnionCardProjectItem> items) {
		this.items = items;
	}
}
