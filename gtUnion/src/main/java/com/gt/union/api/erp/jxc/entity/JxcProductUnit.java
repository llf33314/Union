package com.gt.union.api.erp.jxc.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author hongjiye
 * @time 2017-12-07 15:07
 **/
@ApiModel(value = "进销存商品副单位实体")
public class JxcProductUnit {

	/**
	 * 副单位id
	 */
	@ApiModelProperty(value = "副单位id")
	private Integer unitId;

	/**
	 * 副单位名称
	 */
	@ApiModelProperty(value = "副单位名称")
	private String unitName;

	/**
	 * 销售单价
	 */
	@ApiModelProperty(value = "销售单价（元）")
	private Double unitPrice;

	/**
	 * 主副单位换算率(副单位数量 * 换算率 = 销售数量)  如1箱水有24支  主单位是想，副单位是支 那么换算率就是1/24
	 */
	@ApiModelProperty(value = "主副单位换算率(副单位数量 * 换算率 = 销售数量)  如1箱水有24支  主单位是想，副单位是支 那么换算率就是1/24")
	private Double convertRatio;

	/**
	 * 条码
	 */
	@ApiModelProperty(value = "条码")
	private String barCode;

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Double getConvertRatio() {
		return convertRatio;
	}

	public void setConvertRatio(Double convertRatio) {
		this.convertRatio = convertRatio;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
}