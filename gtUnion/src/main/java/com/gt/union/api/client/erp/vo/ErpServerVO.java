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
	private Integer serverId;

	@ApiModelProperty(value = "erp服务项目名称")
	private String serverName;

	public Integer getServerId() {
		return serverId;
	}

	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
}
