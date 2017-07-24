package com.gt.union.entity.consume;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 会员消费
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@TableName("t_union_consume_record")
public class UnionConsumeRecord extends Model<UnionConsumeRecord> {

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
     * 创建时间
     */
	private Date createtime;
    /**
     * 联盟卡
     */
	@TableField("union_card_id")
	private Integer unionCardId;
    /**
     * 状态（0：未支付 1：已支付 2：已退款）
     */
	private Integer status;
    /**
     * 消费类型（1：线上 2：线下）
     */
	private Integer type;
    /**
     * 消费总额
     */
	@TableField("total_money")
	private Double totalMoney;
    /**
     * 支付金额
     */
	@TableField("pay_money")
	private Double payMoney;
    /**
     * 消费的商家id
     */
	@TableField("bus_id")
	private Integer busId;
    /**
     * 订单号
     */
	@TableField("order_no")
	private String orderNo;
    /**
     * 订单类型
     */
	@TableField("order_type")
	private Integer orderType;
    /**
     * 联盟id
     */
	@TableField("union_id")
	private Integer unionId;
    /**
     * 微信订单号
     */
	@TableField("wx_order_no")
	private String wxOrderNo;
    /**
     * 线上消费行业类型（0：线下 大于0：行业）
     */
	private Integer model;
    /**
     * 行业消费描述
     */
	@TableField("model_desc")
	private String modelDesc;
    /**
     * 行业订单id
     */
	@TableField("mode_order_id")
	private Integer modeOrderId;
    /**
     * 是否赠送了积分（0：未赠送 1：赠送）
     */
	@TableField("is_give_integral")
	private Integer isGiveIntegral;
    /**
     * 核销类型（0：金额消费 1：优惠服务）
     */
	@TableField("consume_type")
	private Integer consumeType;


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

	public Integer getUnionCardId() {
		return unionCardId;
	}

	public void setUnionCardId(Integer unionCardId) {
		this.unionCardId = unionCardId;
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

	public Double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public Double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public String getWxOrderNo() {
		return wxOrderNo;
	}

	public void setWxOrderNo(String wxOrderNo) {
		this.wxOrderNo = wxOrderNo;
	}

	public Integer getModel() {
		return model;
	}

	public void setModel(Integer model) {
		this.model = model;
	}

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}

	public Integer getModeOrderId() {
		return modeOrderId;
	}

	public void setModeOrderId(Integer modeOrderId) {
		this.modeOrderId = modeOrderId;
	}

	public Integer getIsGiveIntegral() {
		return isGiveIntegral;
	}

	public void setIsGiveIntegral(Integer isGiveIntegral) {
		this.isGiveIntegral = isGiveIntegral;
	}

	public Integer getConsumeType() {
		return consumeType;
	}

	public void setConsumeType(Integer consumeType) {
		this.consumeType = consumeType;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
