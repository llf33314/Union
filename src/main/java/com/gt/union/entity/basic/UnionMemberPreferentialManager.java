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
 * 盟员优惠项目管理
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@TableName("t_union_member_preferential_manager")
public class UnionMemberPreferentialManager extends Model<UnionMemberPreferentialManager> {

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
     * 最后修改时间
     */
	@TableField("last_modify_time")
	private Date lastModifyTime;
    /**
     * 盟员id
     */
	@TableField("member_id")
	private Integer memberId;
    /**
     * 优惠项目说明
     */
	@TableField("preferential_illustration")
	private String preferentialIllustration;
    /**
     * 审核状态（0：未提交 1：未审核 2：审核通过 3：审核不过）
     */
	@TableField("verify_status")
	private Integer verifyStatus;
    /**
     * 联盟id
     */
	@TableField("union_id")
	private Integer unionId;


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

	public Date getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getPreferentialIllustration() {
		return preferentialIllustration;
	}

	public void setPreferentialIllustration(String preferentialIllustration) {
		this.preferentialIllustration = preferentialIllustration;
	}

	public Integer getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(Integer verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
