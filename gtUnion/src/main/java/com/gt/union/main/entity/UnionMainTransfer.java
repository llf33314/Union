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
 * 联盟转移
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_main_transfer")
public class UnionMainTransfer extends Model<UnionMainTransfer> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
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
     * 转移时间
     */
	private Date createtime;
    /**
     * 转移盟员id(此时仍然是盟主)
     */
	@TableField("from_member_id")
	private Integer fromMemberId;
    /**
     * 被转移盟员id
     */
	@TableField("to_member_id")
	private Integer toMemberId;
    /**
     * 确认状态（1：确认中  2：已确认 3：已拒绝）
     */
	@TableField("confirm_status")
	private Integer confirmStatus;
    /**
     * 是否提示（0：否  1：是）
     */
	@TableField("is_advice")
	private Integer isAdvice;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Integer getFromMemberId() {
		return fromMemberId;
	}

	public void setFromMemberId(Integer fromMemberId) {
		this.fromMemberId = fromMemberId;
	}

	public Integer getToMemberId() {
		return toMemberId;
	}

	public void setToMemberId(Integer toMemberId) {
		this.toMemberId = toMemberId;
	}

	public Integer getConfirmStatus() {
		return confirmStatus;
	}

	public void setConfirmStatus(Integer confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	public Integer getIsAdvice() {
		return isAdvice;
	}

	public void setIsAdvice(Integer isAdvice) {
		this.isAdvice = isAdvice;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
