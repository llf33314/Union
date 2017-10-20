package com.gt.union.card.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 办理联盟卡
 * Created by Administrator on 2017/9/20 0020.
 */
@ApiModel(value = "UnionCardBindParamVO", description = "商机推荐实体")
@Data
public class UnionCardBindParamVO {

	private Integer busId;

	@ApiModelProperty(value = "联盟id", required = true)
	@NotNull(message = "请选择联盟")
	private Integer unionId;

	@ApiModelProperty(value = "联盟卡类型 1：黑卡 2：红卡", required = true)
	@NotNull(message = "请选择连卡类型")
	private Integer cardType;

	@ApiModelProperty(value = "手机号", required = true)
	@NotBlank(message = "手机号不能为空")
	@Pattern(regexp = "^1[3|4|5|6|7|8][0-9][0-9]{8}$", message = "手机号有误")
	private String phone;

	@ApiModelProperty(value = "是否需要关注 1：是 0：否")
	private Boolean follow;

	@ApiModelProperty(value = "关注后选择的粉丝信息id")
	private Integer memberId;


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

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Boolean getFollow() {
		return follow;
	}

	public void setFollow(Boolean follow) {
		this.follow = follow;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
}
