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
 * 联盟盟主转移记录
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@TableName("t_union_transfer_record")
public class UnionTransferRecord extends Model<UnionTransferRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
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
     * 转移商家id
     */
	@TableField("from_bus_id")
	private Integer fromBusId;
    /**
     * 被转移商家id
     */
	@TableField("to_bus_id")
	private Integer toBusId;
    /**
     * 删除状态（0：未删除 1：删除）
     */
	@TableField("del_status")
	private Integer delStatus;
    /**
     * 审核状态（0：未审核  1：确认 2：拒绝）
     */
	@TableField("confirm_status")
	private Integer confirmStatus;
    /**
     * 不再提示（0：提示  1：不提示）
     */
	@TableField("no_advice")
	private Integer noAdvice;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getFromBusId() {
		return fromBusId;
	}

	public void setFromBusId(Integer fromBusId) {
		this.fromBusId = fromBusId;
	}

	public Integer getToBusId() {
		return toBusId;
	}

	public void setToBusId(Integer toBusId) {
		this.toBusId = toBusId;
	}

	public Integer getDelStatus() {
		return delStatus;
	}

	public void setDelStatus(Integer delStatus) {
		this.delStatus = delStatus;
	}

	public Integer getConfirmStatus() {
		return confirmStatus;
	}

	public void setConfirmStatus(Integer confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	public Integer getNoAdvice() {
		return noAdvice;
	}

	public void setNoAdvice(Integer noAdvice) {
		this.noAdvice = noAdvice;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
