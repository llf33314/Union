package com.gt.union.consume.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 消费
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_consume")
public class UnionConsume extends Model<UnionConsume> {

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
	@TableField("card_id")
	private Integer cardId;
    /**
     * 状态（1：未支付 2：已支付 3：已退款）
     */
	private Integer status;
    /**
     * 消费类型（1：线上 2：线下）
     */
	private Integer type;
    /**
     * 消费总额
     */
	@TableField("consume_money")
	private Double consumeMoney;
    /**
     * 支付金额
     */
	@TableField("pay_money")
	private Double payMoney;
    /**
     * 消费的盟员id
     */
	@TableField("member_id")
	private Integer memberId;
    /**
     * 订单号
     */
	@TableField("order_no")
	private String orderNo;
    /**
     * 支付方式（0：现金 1：微信 2：支付宝）
     */
	@TableField("pay_type")
	private Integer payType;
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
	@TableField("model_order_id")
	private Integer modelOrderId;
    /**
     * 是否赠送积分（0：未赠送 1：赠送）
     */
	@TableField("is_integral")
	private Integer isIntegral;
    /**
     * 门店id
     */
	@TableField("shop_id")
	private Integer shopId;


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

	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
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

	public Double getConsumeMoney() {
		return consumeMoney;
	}

	public void setConsumeMoney(Double consumeMoney) {
		this.consumeMoney = consumeMoney;
	}

	public Double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
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

	public Integer getModelOrderId() {
		return modelOrderId;
	}

	public void setModelOrderId(Integer modelOrderId) {
		this.modelOrderId = modelOrderId;
	}

	public Integer getIsIntegral() {
		return isIntegral;
	}

	public void setIsIntegral(Integer isIntegral) {
		this.isIntegral = isIntegral;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
