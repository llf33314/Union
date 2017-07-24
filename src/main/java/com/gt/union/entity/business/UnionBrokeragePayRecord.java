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
 * 佣金支付到平台记录
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@TableName("t_union_brokerage_pay_record")
public class UnionBrokeragePayRecord extends Model<UnionBrokeragePayRecord> {

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
     * 支付商家（需要支付佣金的商家）
     */
	@TableField("pay_bus_id")
	private Integer payBusId;
    /**
     * 收款商家（应得佣金的商家）
     */
	@TableField("obtain_bus_id")
	private Integer obtainBusId;
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
     * 联盟id
     */
	@TableField("union_id")
	private Integer unionId;
    /**
     * 用户id
     */
	@TableField("member_id")
	private Integer memberId;
    /**
     * 商机推荐ids，逗号间隔
     */
	@TableField("recommend_ids")
	private String recommendIds;


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

	public Integer getPayBusId() {
		return payBusId;
	}

	public void setPayBusId(Integer payBusId) {
		this.payBusId = payBusId;
	}

	public Integer getObtainBusId() {
		return obtainBusId;
	}

	public void setObtainBusId(Integer obtainBusId) {
		this.obtainBusId = obtainBusId;
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

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getRecommendIds() {
		return recommendIds;
	}

	public void setRecommendIds(String recommendIds) {
		this.recommendIds = recommendIds;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
