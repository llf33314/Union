package com.gt.union.api.client.erp.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author hongjiye
 * @time 2017-12-08 10:42
 **/
@ApiModel(value = "erp行业类型")
public class ErpTypeVO {

	@ApiModelProperty(value = "erp行业类型 前端")
	private Integer erpType;

	@ApiModelProperty(value = "erp行业类型 后端")
	private Integer erpModel;

	@ApiModelProperty(value = "erp行业名称")
	private String erpName;

	public Integer getErpType() {
		return erpType;
	}

	public void setErpType(Integer erpType) {
		this.erpType = erpType;
	}

	public String getErpName() {
		return erpName;
	}

	public void setErpName(String erpName) {
		this.erpName = erpName;
	}

	public Integer getErpModel() {
		return erpModel;
	}

	public void setErpModel(Integer erpModel) {
		this.erpModel = erpModel;
	}
}
