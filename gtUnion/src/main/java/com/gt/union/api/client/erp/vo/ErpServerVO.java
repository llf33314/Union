package com.gt.union.api.client.erp.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author hongjiye
 * @time 2017-12-19 9:26
 **/
@ApiModel(value = "erp项目")
public class ErpServerVO {

	@ApiModelProperty(value = "erp服务项目id")
	private Integer id;

	@ApiModelProperty(value = "erp服务项目名称")
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
