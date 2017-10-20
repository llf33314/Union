package com.gt.union.verifier.vo;

import com.gt.union.common.annotation.valid.StringLengthValid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * Created by Administrator on 2017/10/11 0011.
 */
@ApiModel(value = "UnionVerifierVO", description = "佣金平台管理员")
@Data
public class UnionVerifierVO {

	private static final long serialVersionUID = 1L;

	private Integer busId;

	/**
	 * 电话号码
	 */
	@ApiModelProperty(value = "手机号", required = true)
	@NotBlank(message = "手机号不能为空")
	@Pattern(regexp = "^1[3|4|5|6|7|8][0-9][0-9]{8}$", message = "手机号有误")
	private String phone;
	/**
	 * 用户姓名
	 */
	@ApiModelProperty(value = "姓名", required = true)
	@NotBlank(message = "姓名不能为空")
	@StringLengthValid(length = 10, message = "姓名不可超过10字")
	private String name;

	/**
	 * 验证码
	 */
	@ApiModelProperty(value = "验证码", required = true)
	@NotBlank(message = "验证码不能为空")
	private String code;

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
