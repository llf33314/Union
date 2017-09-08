package com.gt.union.brokerage.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 商家收支表
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_income_expense")
public class UnionIncomeExpense extends Model<UnionIncomeExpense> {

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
     * 类型（1：收入 2：支出）
     */
	private Integer type;
    /**
     * 商家id
     */
	@TableField("bus_id")
	private Integer busId;
    /**
     * 联盟id
     */
	@TableField("union_id")
	private Integer unionId;
    /**
     * 金额
     */
	private Double price;
    /**
     * 佣金收入id
     */
	@TableField("brokerage_income_id")
	private Integer brokerageIncomeId;
    /**
     * 佣金支出id
     */
	@TableField("brokerage_pay_id")
	private Integer brokeragePayId;


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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getBrokerageIncomeId() {
		return brokerageIncomeId;
	}

	public void setBrokerageIncomeId(Integer brokerageIncomeId) {
		this.brokerageIncomeId = brokerageIncomeId;
	}

	public Integer getBrokeragePayId() {
		return brokeragePayId;
	}

	public void setBrokeragePayId(Integer brokeragePayId) {
		this.brokeragePayId = brokeragePayId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
