package com.gt.union.entity.card;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 购买升级联盟卡记录
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@TableName("t_union_upgrade_card_record")
public class UnionUpgradeCardRecord extends Model<UnionUpgradeCardRecord> {

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
     * 支付的用户id
     */
	@TableField("member_id")
	private Integer memberId;
    /**
     * 联盟卡信息id
     */
	@TableField("card_info_id")
	private Integer cardInfoId;
    /**
     * 订单号
     */
	@TableField("order_no")
	private String orderNo;
    /**
     * 微信订单号
     */
	@TableField("wx_order_no")
	private String wxOrderNo;
    /**
     * 支付状态（1：未支付 2：支付成功 3：支付失败 4：已退款 5：不用支付）
     */
	@TableField("pay_status")
	private Integer payStatus;
    /**
     * 支付类型（1：微信支付 2：支付宝支付 3：不用支付）
     */
	@TableField("pay_type")
	private Integer payType;
    /**
     * 支付的商家id
     */
	@TableField("pay_bus_id")
	private Integer payBusId;
    /**
     * 订单描述
     */
	@TableField("order_desc")
	private String orderDesc;
    /**
     * 联盟id
     */
	@TableField("union_id")
	private Integer unionId;
    /**
     * 支付金额
     */
	@TableField("pay_money")
	private Double payMoney;


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

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getCardInfoId() {
		return cardInfoId;
	}

	public void setCardInfoId(Integer cardInfoId) {
		this.cardInfoId = cardInfoId;
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

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getPayBusId() {
		return payBusId;
	}

	public void setPayBusId(Integer payBusId) {
		this.payBusId = payBusId;
	}

	public String getOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
