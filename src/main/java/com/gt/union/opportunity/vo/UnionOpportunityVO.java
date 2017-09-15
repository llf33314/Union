package com.gt.union.opportunity.vo;

import com.gt.union.common.annotation.valid.StringLengthValid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by Administrator on 2017/9/13 0013.
 */
@ApiModel( value = "UnionOpportunityVO", description = "商机推荐实体" )
@Data
public class UnionOpportunityVO {

	/**
	 * 推荐的商家id
	 */
	private Integer busId;

	/**
	 * 所属联盟id
	 */
	@NotNull(message = "请选择推荐的联盟")
	@ApiModelProperty( value = "选择的联盟" ,required = true)
	private Integer unionId;

	/**
	 * 推荐的盟员id
	 */
	@ApiModelProperty( value = "推荐的盟员" ,required = true)
	@NotNull(message = "请选择推荐的盟员")
	private Integer toMemberId;

	/**
	 * 用户姓名
	 */
	@ApiModelProperty( value = "意向客户姓名" ,required = true)
	@NotBlank(message = "意向客户姓名不能为空")
	@StringLengthValid(length = 10, message = "意向客户姓名不可超过10字")
	private String clientName;

	/**
	 * 用户电话
	 */
	@ApiModelProperty( value = "意向客户手机" ,required = true)
	@NotBlank(message = "意向客户手机不能为空")
	@Pattern(regexp = "^1[3|4|5|6|7|8][0-9][0-9]{8}$", message = "意向客户手机有误")
	private String clientPhone;

	/**
	 * 业务备注
	 */
	@StringLengthValid(length = 20, message = "业务备注不可超过20字")
	@ApiModelProperty( value = "业务备注")
	private String businessMsg;


	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Integer getToMemberId() {
		return toMemberId;
	}

	public void setToMemberId(Integer toMemberId) {
		this.toMemberId = toMemberId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientPhone() {
		return clientPhone;
	}

	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}

	public String getBusinessMsg() {
		return businessMsg;
	}

	public void setBusinessMsg(String businessMsg) {
		this.businessMsg = businessMsg;
	}
}
