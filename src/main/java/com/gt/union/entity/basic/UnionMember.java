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
 * 联盟成员列表
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@TableName("t_union_member")
public class UnionMember extends Model<UnionMember> {

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
     * 联盟id
     */
	@TableField("union_id")
	private Integer unionId;
    /**
     * 成员id
     */
	@TableField("bus_id")
	private Integer busId;
    /**
     * 是否盟主（0：否 1：是）
     */
	@TableField("is_nuion_owner")
	private Integer isNuionOwner;
    /**
     * 联盟ID标识
     */
	@TableField("union_ID_sign")
	private String unionIDSign;
    /**
     * 商家退出状态(0：正常 1：申请未处理 2：过渡期 )
     */
	@TableField("out_staus")
	private Integer outStaus;
    /**
     * 申请退出时间
     */
	@TableField("apply_out_time")
	private Date applyOutTime;
    /**
     * 确认处理时间
     */
	private Date confirm;
    /**
     * 确认退出时间
     */
	@TableField("confirm_out_time")
	private Date confirmOutTime;
    /**
     * 退盟理由
     */
	@TableField("out_reason")
	private String outReason;


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

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public Integer getIsNuionOwner() {
		return isNuionOwner;
	}

	public void setIsNuionOwner(Integer isNuionOwner) {
		this.isNuionOwner = isNuionOwner;
	}

	public String getUnionIDSign() {
		return unionIDSign;
	}

	public void setUnionIDSign(String unionIDSign) {
		this.unionIDSign = unionIDSign;
	}

	public Integer getOutStaus() {
		return outStaus;
	}

	public void setOutStaus(Integer outStaus) {
		this.outStaus = outStaus;
	}

	public Date getApplyOutTime() {
		return applyOutTime;
	}

	public void setApplyOutTime(Date applyOutTime) {
		this.applyOutTime = applyOutTime;
	}

	public Date getConfirm() {
		return confirm;
	}

	public void setConfirm(Date confirm) {
		this.confirm = confirm;
	}

	public Date getConfirmOutTime() {
		return confirmOutTime;
	}

	public void setConfirmOutTime(Date confirmOutTime) {
		this.confirmOutTime = confirmOutTime;
	}

	public String getOutReason() {
		return outReason;
	}

	public void setOutReason(String outReason) {
		this.outReason = outReason;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
