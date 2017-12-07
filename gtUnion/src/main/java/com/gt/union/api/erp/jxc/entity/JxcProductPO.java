package com.gt.union.api.erp.jxc.entity;

import java.util.List;

/**
 * 进销存商品实体
 * @author hongjiye
 * @time 2017-12-07 15:04
 **/
public class JxcProductPO {

	/**
	 * 商品id
	 */
	private Integer id;

	/**
	 * 商品名称
	 */
	private String name;

	/**
	 * 商品条码
	 */
	private String barCode;

	/**
	 * 库存数量
	 */
	private Integer amount;

	/**
	 * 商品编码
	 */
	private String proCode;

	/**
	 * 商品主单位名称
	 */
	private String unitName;

	/**
	 * 商品规格
	 */
	private String spec;

	/**
	 * 商品主单位id
	 */
	private Integer unitId;

	/**
	 * 商品分类名称
	 */
	private String type;

	/**
	 * 所属门店名称
	 */
	private String warehouse;

	/**
	 * 零售价(元)
	 */
	private Double retailPrice;

	/**
	 * 成本价(元)
	 */
	private Double costPrice;

	/**
	 * 是否允许打折 true：允许  false：否
	 */
	private Boolean discount;

	/**
	 * 商品副单位列表
	 */
	private List<JxcProductUnitPO> viceUnits;

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

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getProCode() {
		return proCode;
	}

	public void setProCode(String proCode) {
		this.proCode = proCode;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public Double getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}

	public Double getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(Double costPrice) {
		this.costPrice = costPrice;
	}

	public Boolean getDiscount() {
		return discount;
	}

	public void setDiscount(Boolean discount) {
		this.discount = discount;
	}

	public List<JxcProductUnitPO> getViceUnits() {
		return viceUnits;
	}

	public void setViceUnits(List<JxcProductUnitPO> viceUnits) {
		this.viceUnits = viceUnits;
	}
}
