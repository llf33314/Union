package com.gt.union.api.server.entity.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 绑定联盟卡结果
 * @author hongjiye
 * Created by Administrator on 2017/9/2 0002.
 */
@ApiModel( value = "UnionBindCardResult", description = "绑定联盟卡结果实体" )
@Data
public class UnionBindCardResult {

	@ApiModelProperty(value = "提示信息")
	private String message;

	@ApiModelProperty(value = "绑定状态 true：成功 false：失败")
	private boolean success;

	public UnionBindCardResult(boolean success, String message){
		this.success = success;
		this.message = message;
	}

	public UnionBindCardResult(){
		this.success = false;
		this.message = "绑定失败";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
