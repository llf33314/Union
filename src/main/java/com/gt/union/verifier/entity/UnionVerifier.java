package com.gt.union.verifier.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.gt.union.common.annotation.valid.StringLengthValid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 联盟平台管理人员
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_verifier")
@ApiModel(value = "UnionVerifier", description = "佣金平台管理员")
@Data
public class UnionVerifier extends Model<UnionVerifier> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 商家id
     */
	@TableField("bus_id")
	private Integer busId;
    /**
     * 创建时间
     */
	private Date createtime;
    /**
     * 删除状态（0：未删除 1：删除）
     */
	@TableField("del_status")
	private Integer delStatus;
    /**
     * 用户id
     */
	@TableField("third_member_id")
	private Integer thirdMemberId;
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
	private String code;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Integer getDelStatus() {
		return delStatus;
	}

	public void setDelStatus(Integer delStatus) {
		this.delStatus = delStatus;
	}

	public Integer getThirdMemberId() {
		return thirdMemberId;
	}

	public void setThirdMemberId(Integer thirdMemberId) {
		this.thirdMemberId = thirdMemberId;
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

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
