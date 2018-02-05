package com.gt.union.api.server.entity.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 联盟卡退款参数
 * @author hongjiye
 * Created by Administrator on 2017/9/4 0004.
 */
@ApiModel( value = "UnionRefundParam", description = "其他模块使用了联盟卡退款参数实体" )
@Data
public class UnionRefundParam {

	@ApiModelProperty( value = "订单号", required = true)
	private String orderNo;

	@ApiModelProperty( value = "行业模型，商城：1 ...  需要添加的请告知", required = true)
	private Integer model;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getModel() {
		return model;
	}

	public void setModel(Integer model) {
		this.model = model;
	}
}
