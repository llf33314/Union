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
 * 联盟成员申请推荐
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@TableName("t_union_apply")
public class UnionApply extends Model<UnionApply> {

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
     * 申请商家id
     */
	@TableField("apply_bus_id")
	private Integer applyBusId;
    /**
     * 审核状态（0：未审核 1：审核通过 2：审核不通过）
     */
	@TableField("apply_status")
	private Integer applyStatus;
    /**
     * 申请联盟id
     */
	@TableField("union_id")
	private Integer unionId;
    /**
     * 申请方式（1：自由申请 2：推荐申请）
     */
	@TableField("apply_type")
	private Integer applyType;
    /**
     * 推荐商家id
     */
	@TableField("recommend_bus_id")
	private Integer recommendBusId;
    /**
     * 商家确认状态(0：申请状态 1：未确认 2：确认通过 3：拒绝)
     */
	@TableField("bus_confirm_status")
	private Integer busConfirmStatus;
    /**
     * 联盟成员id
     */
	@TableField("union_member_id")
	private Integer unionMemberId;

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

	public Integer getApplyBusId() {
		return applyBusId;
	}

	public void setApplyBusId(Integer applyBusId) {
		this.applyBusId = applyBusId;
	}

	public Integer getApplyStatus() {
		return applyStatus;
	}

	public void setApplyStatus(Integer applyStatus) {
		this.applyStatus = applyStatus;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Integer getApplyType() {
		return applyType;
	}

	public void setApplyType(Integer applyType) {
		this.applyType = applyType;
	}

	public Integer getRecommendBusId() {
		return recommendBusId;
	}

	public void setRecommendBusId(Integer recommendBusId) {
		this.recommendBusId = recommendBusId;
	}

	public Integer getBusConfirmStatus() {
		return busConfirmStatus;
	}

	public void setBusConfirmStatus(Integer busConfirmStatus) {
		this.busConfirmStatus = busConfirmStatus;
	}

	public Integer getUnionMemberId() {
		return unionMemberId;
	}

	public void setUnionMemberId(Integer unionMemberId) {
		this.unionMemberId = unionMemberId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
