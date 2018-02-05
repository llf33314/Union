package com.gt.union.api.server.entity.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * api请求参数
 * @author hongjiye
 * Created by Administrator on 2017/9/4 0004.
 */
@ApiModel( value = "RequestApiParam", description = "接口参数" )
@Data
public class RequestApiParam<T> implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 业务请求参数
	 */
	@ApiModelProperty( value = "业务请求参数",required = true)
	private T reqdata;

	public T getReqdata() {
		return reqdata;
	}

	public void setReqdata(T reqdata) {
		this.reqdata = reqdata;
	}
}
