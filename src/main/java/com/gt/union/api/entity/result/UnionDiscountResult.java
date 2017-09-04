package com.gt.union.api.entity.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/23 0023.
 */
@ApiModel( value = "UnionDiscountResult", description = "获取联盟折扣实体" )
@Data
public class UnionDiscountResult implements Serializable{

	public static int UNION_DISCOUNT_CODE_NON = -1;//不显示
	public static int UNION_DISCOUNT_CODE_BIND = 0;//需绑定联盟卡
	public static int UNION_DISCOUNT_CODE_SUCCESS = 1;//返回折扣

	/**
	 * 状态码信息
	 */
	@ApiModelProperty( value = "状态码信息 -1：没有联盟信息，不显示 0：没有关联联盟卡，需绑定联盟卡 1：返回联盟折扣")
	private Integer code;

	/**
	 * 是否默认折扣
	 */
	@ApiModelProperty( value = "是否默认折扣 false：否 true：是")
	private Boolean ifDefault;

	/**
	 * 升级的联盟卡id
	 */
	@ApiModelProperty( value = "升级的联盟卡id")
	private Integer cardId;

	/**
	 * 联盟折扣
	 */
	@ApiModelProperty( value = "联盟卡折扣")
	private Double discount;


	public UnionDiscountResult(){

	}

	public UnionDiscountResult(int code){
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Boolean getIfDefault() {
		return ifDefault;
	}

	public void setIfDefault(Boolean ifDefault) {
		this.ifDefault = ifDefault;
	}

	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}
}
