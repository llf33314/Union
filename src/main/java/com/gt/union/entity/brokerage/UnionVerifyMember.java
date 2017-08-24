package com.gt.union.entity.brokerage;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.gt.union.common.annotation.valid.StringLengthValid;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 联盟佣金平台管理人员
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@TableName("t_union_verify_member")
@ApiModel( value = "UnionVerifyMember", description = "佣金平台管理人员实体" )
@Data
public class UnionVerifyMember extends Model<UnionVerifyMember> {

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
	@TableField("member_id")
	private Integer memberId;
    /**
     * 电话号码
     */
	@NotBlank(message = "手机号码不能为空")
	@Pattern(regexp = "^1[3|4|5|6|7|8][0-9][0-9]{8}$", message = "手机号码有误")
	private String phone;
    /**
     * 用户姓名
     */
	@TableField("member_name")
	@NotBlank(message = "姓名不能为空")
	@StringLengthValid(length = 5,message = "姓名不可超过5字")
	private String memberName;

	/**
	 * 验证码
	 */
	@NotBlank(message = "验证码不能为空")
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

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
