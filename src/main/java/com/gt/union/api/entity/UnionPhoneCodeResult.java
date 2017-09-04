package com.gt.union.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Administrator on 2017/9/2 0002.
 */
@ApiModel( value = "UnionPhoneCodeResult", description = "获取绑定联盟卡验证码" )
@Data
public class UnionPhoneCodeResult {


	@ApiModelProperty(value = "发送状态 true：成功 false：失败")
	private boolean success;

	@ApiModelProperty(value = "提示信息")
	private String message;


	public UnionPhoneCodeResult(boolean success, String message){
		this.success = success;
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
