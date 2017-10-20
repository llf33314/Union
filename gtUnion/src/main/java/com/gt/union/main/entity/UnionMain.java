package com.gt.union.main.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 联盟主表
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_main")
public class UnionMain extends Model<UnionMain> {

    private static final long serialVersionUID = 1L;

    /**
     * 主表
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
     * 联盟名称
     */
	private String name;
    /**
     * 联盟图标
     */
	private String img;
    /**
     * 加盟方式（1：推荐 2：申请、推荐）
     */
	@TableField("join_type")
	private Integer joinType;
    /**
     * 联盟说明
     */
	private String illustration;
    /**
     * 联盟成员总数上限
     */
	@TableField("limit_member")
	private Integer limitMember;
    /**
     * 联盟是否开启积分（0：否 1：是）
     */
	@TableField("is_integral")
	private Integer isIntegral;
    /**
     * 联盟有效期
     */
	@TableField("union_validity")
	private Date unionValidity;


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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Integer getJoinType() {
		return joinType;
	}

	public void setJoinType(Integer joinType) {
		this.joinType = joinType;
	}

	public String getIllustration() {
		return illustration;
	}

	public void setIllustration(String illustration) {
		this.illustration = illustration;
	}

	public Integer getLimitMember() {
		return limitMember;
	}

	public void setLimitMember(Integer limitMember) {
		this.limitMember = limitMember;
	}

	public Integer getIsIntegral() {
		return isIntegral;
	}

	public void setIsIntegral(Integer isIntegral) {
		this.isIntegral = isIntegral;
	}

	public Date getUnionValidity() {
		return unionValidity;
	}

	public void setUnionValidity(Date unionValidity) {
		this.unionValidity = unionValidity;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
