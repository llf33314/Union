package com.gt.union.member.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 联盟成员入盟申请
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_member_join")
public class UnionMemberJoin extends Model<UnionMemberJoin> {

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
     * 删除状态（0：否 1：是）
     */
	@TableField("del_status")
	private Integer delStatus;
    /**
     * 申请加入的联盟成员id
     */
	@TableField("apply_member_id")
	private Integer applyMemberId;
    /**
     * 推荐加入的联盟成员id
     */
	@TableField("recommend_member_id")
	private Integer recommendMemberId;
    /**
     * 加入或推荐理由
     */
	private String reason;
    /**
     * 是否同意推荐(0：不同意 1：同意)
     */
	@TableField("is_recommend_agree")
	private Integer isRecommendAgree;
    /**
     * 入盟类型（1：申请 2：推荐）
     */
	private Integer type;


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

	public Integer getApplyMemberId() {
		return applyMemberId;
	}

	public void setApplyMemberId(Integer applyMemberId) {
		this.applyMemberId = applyMemberId;
	}

	public Integer getRecommendMemberId() {
		return recommendMemberId;
	}

	public void setRecommendMemberId(Integer recommendMemberId) {
		this.recommendMemberId = recommendMemberId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Integer getIsRecommendAgree() {
		return isRecommendAgree;
	}

	public void setIsRecommendAgree(Integer isRecommendAgree) {
		this.isRecommendAgree = isRecommendAgree;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
