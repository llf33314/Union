package com.gt.union.api.entity.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/4 0004.
 */
@ApiModel( value = "UnionConsumeParam", description = "其他模块使用联盟卡核销参数实体" )
@Data
public class UnionConsumeParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty( value = "消费的商家id", required = true)
	private Integer busId;

	@ApiModelProperty( value = "行业模型，商城：1 ...  需要添加的请告知", required = true)
	private Integer model;

	@ApiModelProperty( value = "消费的订单描述", required = true)
	private String modelDesc;

	@ApiModelProperty( value = "订单号", required = true)
	private String orderNo;

	@ApiModelProperty( value = "是否可以立刻赠送积分，默认立刻赠送，否则请填0")
	private Boolean giveIntegralNow;

	@ApiModelProperty( value = "线上或线下使用联盟卡 1：线下 2：线上", required = true)
	private Integer type;

	@ApiModelProperty( value = "使用联盟卡打折前的价格", required = true)
	private Double totalMoney;

	@ApiModelProperty( value = "使用联盟卡打折后的价格", required = true)
	private Double payMoney;

	@ApiModelProperty( value = "支付方式：0：现金 1：微信 2：支付宝， 若有其他支付方式，请告知")
	private Integer orderType;

	@ApiModelProperty( value = "联盟id")
	private Integer unionId;

	@ApiModelProperty( value = "联盟卡id")
	private Integer unionCardId;

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public Integer getModel() {
		return model;
	}

	public void setModel(Integer model) {
		this.model = model;
	}

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Boolean getGiveIntegralNow() {
		return giveIntegralNow;
	}

	public void setGiveIntegralNow(Boolean giveIntegralNow) {
		this.giveIntegralNow = giveIntegralNow;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public Double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Integer getUnionCardId() {
		return unionCardId;
	}

	public void setUnionCardId(Integer unionCardId) {
		this.unionCardId = unionCardId;
	}
}
