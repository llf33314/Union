package com.gt.union.api.client.erp.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author hongjiye
 * @time 2017-12-08 10:42
 **/
@ApiModel(value = "erp行业类型")
public class ErpModelVO {

	@ApiModelProperty(value = "erp行业类型")
	private Integer erpModel;

	@ApiModelProperty(value = "erp行业名称")
	private String erpName;

	public Integer getErpModel() {
		return erpModel;
	}

	public void setErpModel(Integer erpModel) {
		this.erpModel = erpModel;
	}

	public String getErpName() {
		return erpName;
	}

	public void setErpName(String erpName) {
		this.erpName = erpName;
	}

	@Override
	public String toString() {
		return "ErpModelVO{" +
				"erpModel=" + erpModel +
				", erpName='" + erpName + '\'' +
				'}';
	}
}
