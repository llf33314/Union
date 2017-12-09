package com.gt.union.api.server.entity.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/13 0013.
 */
@ApiModel( value = "UnionCardDiscountParam", description = "其他模块线上获取联盟卡折扣参数实体" )
@Data
public class UnionCardDiscountParam implements Serializable {

	@ApiModelProperty( value = "粉丝用户id", required = true)
	private Integer memberId;

	@ApiModelProperty( value = "消费的商家id", required = true)
	private Integer busId;

	@ApiModelProperty( value = "粉丝用户的手机号，可以为空", required = true)
	private String phone;

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
