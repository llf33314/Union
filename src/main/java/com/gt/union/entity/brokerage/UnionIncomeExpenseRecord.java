package com.gt.union.entity.brokerage;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 商家收支记录表
 * </p>
 *
 * @author linweicong
 * @since 2017-08-28
 */
@TableName("t_union_income_expense_record")
public class UnionIncomeExpenseRecord extends Model<UnionIncomeExpenseRecord> {

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
     * 收支类型（1：收入 2：支出）
     */
	private Integer type;
    /**
     * 收支来源（1：商机推荐 2：售卡分成 3：提现）
     */
	private Integer source;
    /**
     * 收支金额
     */
	private Double money;
    /**
     * 关联的商机推荐id
     */
	@TableField("business_recommend_id")
	private Integer businessRecommendId;
    /**
     * 关联的售卡分成id
     */
	@TableField("card_divide_record_id")
	private Integer cardDivideRecordId;
    /**
     * 关联的提现记录id
     */
	@TableField("brokerage_withdrawals_record_id")
	private Integer brokerageWithdrawalsRecordId;


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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Integer getBusinessRecommendId() {
		return businessRecommendId;
	}

	public void setBusinessRecommendId(Integer businessRecommendId) {
		this.businessRecommendId = businessRecommendId;
	}

	public Integer getCardDivideRecordId() {
		return cardDivideRecordId;
	}

	public void setCardDivideRecordId(Integer cardDivideRecordId) {
		this.cardDivideRecordId = cardDivideRecordId;
	}

	public Integer getBrokerageWithdrawalsRecordId() {
		return brokerageWithdrawalsRecordId;
	}

	public void setBrokerageWithdrawalsRecordId(Integer brokerageWithdrawalsRecordId) {
		this.brokerageWithdrawalsRecordId = brokerageWithdrawalsRecordId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
