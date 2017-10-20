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
 * 联盟许可，盟主服务
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_main_permit")
public class UnionMainPermit extends Model<UnionMainPermit> {

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
     * 联盟有效期
     */
	private Date validity;
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
	@TableField("pay_type")
	private Integer payType;
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
     * 产品号码
     */
	@TableField("product_id")
	private String productId;
    /**
     * 支付宝订单号
     */
	@TableField("alipay_order_no")
	private String alipayOrderNo;


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

	public Date getValidity() {
		return validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
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

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
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

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
