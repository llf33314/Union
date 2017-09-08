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
 * 佣金支付记录
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_brokerage_pay")
public class UnionBrokeragePay extends Model<UnionBrokeragePay> {

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
     * 支付时间
     */
	private Date createtime;
    /**
     * 支付金额
     */
	private Double money;
    /**
     * 支付盟员（需要支付佣金的盟员）
     */
	@TableField("from_member_id")
	private Integer fromMemberId;
    /**
     * 收款盟员（应得佣金的盟员）
     */
	@TableField("to_member_id")
	private Integer toMemberId;
    /**
     * 支付状态（1：未支付 2：支付成功 3：已退款）
     */
	private Integer status;
    /**
     * 支付类型（1：微信 2：支付宝）
     */
	private Integer type;
    /**
     * 订单号
     */
	@TableField("order_no")
	private String orderNo;
    /**
     * 微信订单编号
     */
	@TableField("wx_order_no")
	private String wxOrderNo;
    /**
     * 商机推荐id
     */
	@TableField("opportunity_id")
	private String opportunityId;
    /**
     * 佣金平台管理员id
     */
	@TableField("verifier_id")
	private Integer verifierId;


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

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getWxOrderNo() {
		return wxOrderNo;
	}

	public void setWxOrderNo(String wxOrderNo) {
		this.wxOrderNo = wxOrderNo;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public Integer getVerifierId() {
		return verifierId;
	}

	public void setVerifierId(Integer verifierId) {
		this.verifierId = verifierId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
