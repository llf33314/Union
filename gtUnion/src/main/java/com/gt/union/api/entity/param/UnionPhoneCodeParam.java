package com.gt.union.api.entity.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/13 0013.
 */
@ApiModel( value = "UnionPhoneCodeParam", description = "其他模块线上获取验证码参数实体" )
@Data
public class UnionPhoneCodeParam implements Serializable {

	@ApiModelProperty( value = "粉丝用户id", required = true)
	private Integer memberId;

	@ApiModelProperty( value = "商家id", required = true)
	private Integer busId;

	@ApiModelProperty( value = "粉丝用户的手机号", required = true)
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
