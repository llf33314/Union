package com.gt.union.entity.business;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 联盟商家佣金比率
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@TableName("t_union_brokerage")
public class UnionBrokerage extends Model<UnionBrokerage> {

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
     * 设置佣金比率商家
     */
	@TableField("from_bus_id")
	private Integer fromBusId;
    /**
     * 受惠佣金比率商家
     */
	@TableField("to_bus_id")
	private Integer toBusId;
    /**
     * 佣金比率（百分比）
     */
	@TableField("brokerage_ratio")
	private Double brokerageRatio;
    /**
     * 修改时间
     */
	private Date modifytime;


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

	public Double getBrokerageRatio() {
		return brokerageRatio;
	}

	public void setBrokerageRatio(Double brokerageRatio) {
		this.brokerageRatio = brokerageRatio;
	}

	public Date getModifytime() {
		return modifytime;
	}

	public void setModifytime(Date modifytime) {
		this.modifytime = modifytime;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
