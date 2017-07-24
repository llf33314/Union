package com.gt.union.entity.basic;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 盟员优惠服务项
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@TableName("t_union_member_preferential_service")
public class UnionMemberPreferentialService extends Model<UnionMemberPreferentialService> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
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
     * 盟员id
     */
	@TableField("union_member_id")
	private Integer unionMemberId;
    /**
     * 服务名称
     */
	@TableField("service_name")
	private String serviceName;
    /**
     * 审核状态（0：未提交 1：未审核 2：审核通过 3：审核不过）
     */
	@TableField("verify_status")
	private Integer verifyStatus;
    /**
     * 优惠管理id
     */
	@TableField("manager_id")
	private Integer managerId;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getUnionMemberId() {
		return unionMemberId;
	}

	public void setUnionMemberId(Integer unionMemberId) {
		this.unionMemberId = unionMemberId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Integer getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(Integer verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
