package com.gt.union.entity.basic;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import java.util.Date;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 创建联盟服务记录
 * </p>
 *
 * @author linweicong
 * @since 2017-08-11
 */
@TableName("t_union_create_info_record")
public class UnionCreateInfoRecord extends Model<UnionCreateInfoRecord> {

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
     * 商家id
     */
	@TableField("bus_id")
	private Integer busId;
    /**
     * 创建联盟状态（0：未创建 1：创建成功）
     */
	@TableField("create_status")
	private Integer createStatus;
    /**
     * 联盟有效期
     */
	@TableField("period_validity")
	private Date periodValidity;
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

	public Integer getDelStatus() {
		return delStatus;
	}

	public void setDelStatus(Integer delStatus) {
		this.delStatus = delStatus;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public Integer getCreateStatus() {
		return createStatus;
	}

	public void setCreateStatus(Integer createStatus) {
		this.createStatus = createStatus;
	}

	public Date getPeriodValidity() {
		return periodValidity;
	}

	public void setPeriodValidity(Date periodValidity) {
		this.periodValidity = periodValidity;
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
