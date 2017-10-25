package com.gt.union.consume.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@ApiModel( value = "UnionConsumeParamVO", description = "联盟卡核销参数实体" )
@Data
public class UnionConsumeParamVO implements Serializable{

	@NotNull(message = "请使用联盟卡")
	@ApiModelProperty( value = "联盟卡id" ,required = true)
	private Integer cardId;


	@NotNull(message = "您不是盟员")
	@ApiModelProperty( value = "盟员id" ,required = true)
	private Integer memberId;

	@NotNull(message = "请选择联盟")
	@ApiModelProperty( value = "联盟id" ,required = true)
	private Integer unionId;

	@ApiModelProperty( value = "门店id" ,required = false)
	private Integer shopId;

	@ApiModelProperty( value = "联盟卡折扣" ,required = false)
	private Double discount;

	@ApiModelProperty( value = "消费金额" ,required = false)
	private Double consumeMoney;

	@ApiModelProperty( value = "实付金额" ,required = false)
	private Double payMoney;

	@ApiModelProperty( value = "是否使用积分抵扣" ,required = false)
	private Boolean useIntegral;

	@ApiModelProperty( value = "消耗的积分" ,required = false)
	private Double consumeIntegral;

	@ApiModelProperty( value = "优惠项目列表" ,required = false)
	private List<Integer> items;

	@ApiModelProperty( value = "支付方式 0：现金 1：微信 2：支付宝" ,required = false)
	private Integer payType;

	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getConsumeMoney() {
		return consumeMoney;
	}

	public void setConsumeMoney(Double consumeMoney) {
		this.consumeMoney = consumeMoney;
	}

	public boolean isUseIntegral() {
		return useIntegral;
	}

	public void setUseIntegral(boolean useIntegral) {
		this.useIntegral = useIntegral;
	}

	public Double getConsumeIntegral() {
		return consumeIntegral;
	}

	public void setConsumeIntegral(Double consumeIntegral) {
		this.consumeIntegral = consumeIntegral;
	}

	public List<Integer> getItems() {
		return items;
	}

	public void setItems(List<Integer> items) {
		this.items = items;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}
}
