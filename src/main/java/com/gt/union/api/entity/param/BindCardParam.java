package com.gt.union.api.entity.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/4 0004.
 */
@ApiModel( value = "BindCardParam", description = "绑定联盟卡参数实体" )
@Data
public class BindCardParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty( value = "电话号码",required = true)
	private String phone;

	@ApiModelProperty( value = "验证码",required = true)
	private String code;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
