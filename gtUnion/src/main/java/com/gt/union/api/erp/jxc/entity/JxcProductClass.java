package com.gt.union.api.erp.jxc.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 进销存商品分类 树形结构
 * @author hongjiye
 * @time 2017-12-07 11:53
 **/
@ApiModel(value = "进销存商品分类")
public class JxcProductClass {

	/**
	 * 分类id
	 */
	@ApiModelProperty(value = "分类id")
	private Integer id;

	/**
	 * 分类名称
	 */
	@ApiModelProperty(value = "分类名称")
	private String name;

	/**
	 * 创建分类时间字符串  yyyy-MM-DD HH:mm
	 */
	@ApiModelProperty(value = "创建分类时间字符串")
	private String createDateStr;

	/**
	 * 创建分类时间
	 */
	@ApiModelProperty(value = "创建分类时间")
	private Date createDate;

	/**
	 * 是否下架(商品按分类下架使用)  true：未下架 可以使用
	 */
	@ApiModelProperty(value = "是否下架(商品按分类下架使用)  true：未下架 可以使用")
	private Boolean sold;

	/**
	 * 是否删除  true：未删除
	 */
	@ApiModelProperty(value = "是否删除  true：未删除")
	private Boolean status;

	/**
	 * 子分类
	 */
	@ApiModelProperty(value = "子分类")
	private List<JxcProductClass> chlid;

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

	public String getCreateDateStr() {
		return createDateStr;
	}

	public void setCreateDateStr(String createDateStr) {
		this.createDateStr = createDateStr;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Boolean getSold() {
		return sold;
	}

	public void setSold(Boolean sold) {
		this.sold = sold;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public List<JxcProductClass> getChlid() {
		return chlid;
	}

	public void setChlid(List<JxcProductClass> chlid) {
		this.chlid = chlid;
	}

	@Override
	public String toString() {
		return "JxcProductClass{" +
				"id=" + id +
				", name='" + name + '\'' +
				", createDateStr='" + createDateStr + '\'' +
				", createDate=" + createDate +
				", sold=" + sold +
				", status=" + status +
				", chlid=" + chlid +
				'}';
	}
}
