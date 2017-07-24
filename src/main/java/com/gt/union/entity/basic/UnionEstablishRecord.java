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
 * 创建联盟交易记录
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@TableName("t_union_establish_record")
public class UnionEstablishRecord extends Model<UnionEstablishRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 内部订单号
     */
	@TableField("sys_order_no")
	private String sysOrderNo;
    /**
     * 微信订单号
     */
	@TableField("wx_order_no")
	private String wxOrderNo;
    /**
     * 商家id
     */
	@TableField("bus_id")
	private Integer busId;
    /**
     * 订单金额
     */
	@TableField("order_money")
	private Double orderMoney;
    /**
     * 订单描述
     */
	@TableField("order_desc")
	private String orderDesc;
    /**
     * 订单状态（1：未支付 2：已支付 3：支付失败）
     */
	@TableField("order_status")
	private Integer orderStatus;
    /**
     * 支付方式（1：微信支付 2：粉币支付 3：支付宝支付）
     */
	@TableField("pay_way")
	private Integer payWay;
    /**
     * 交易类型（1：创建联盟）
     */
	@TableField("pay_tpe")
	private Integer payTpe;
    /**
     * 商家会员id
     */
	@TableField("member_id")
	private Integer memberId;
    /**
     * 交易完成时间
     */
	@TableField("finish_time")
	private Date finishTime;
    /**
     * 产品号码
     */
	@TableField("product_id")
	private String productId;
    /**
     * 支付宝订单号
     */
	@TableField("alipay_order_no")
	private String alipayOrderNo;
    /**
     * 创建联盟的状态(0：未创建联盟 1：创建了联盟)
     */
	@TableField("create_union_status")
	private Integer createUnionStatus;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSysOrderNo() {
		return sysOrderNo;
	}

	public void setSysOrderNo(String sysOrderNo) {
		this.sysOrderNo = sysOrderNo;
	}

	public String getWxOrderNo() {
		return wxOrderNo;
	}

	public void setWxOrderNo(String wxOrderNo) {
		this.wxOrderNo = wxOrderNo;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public Double getOrderMoney() {
		return orderMoney;
	}

	public void setOrderMoney(Double orderMoney) {
		this.orderMoney = orderMoney;
	}

	public String getOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getPayWay() {
		return payWay;
	}

	public void setPayWay(Integer payWay) {
		this.payWay = payWay;
	}

	public Integer getPayTpe() {
		return payTpe;
	}

	public void setPayTpe(Integer payTpe) {
		this.payTpe = payTpe;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getAlipayOrderNo() {
		return alipayOrderNo;
	}

	public void setAlipayOrderNo(String alipayOrderNo) {
		this.alipayOrderNo = alipayOrderNo;
	}

	public Integer getCreateUnionStatus() {
		return createUnionStatus;
	}

	public void setCreateUnionStatus(Integer createUnionStatus) {
		this.createUnionStatus = createUnionStatus;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
