package com.gt.union.consume.vo;

import com.gt.union.consume.entity.UnionConsumeItem;
import com.gt.union.preferential.entity.UnionPreferentialItem;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/15 0015.
 */
public class UnionConsumeVO {

	/**
	 * 盟员名称
	 */
	private String memberName;

	/**
	 * 联盟名称
	 */
	private String unionName;

	/**
	 * 联盟卡号
	 */
	private String cardNo;

	/**
	 * 联盟卡手机号
	 */
	private String phone;

	/**
	 * 消费金额
	 */
	private Double consumeMoney;

	/**
	 * 支付金额
	 */
	private Double payMoney;

	/**
	 * 消费的优惠项目
	 */
	private List<UnionPreferentialItem> items;

	/**
	 * 支付状态 1：未支付 2：已支付 3：已退款
	 */
	private Integer status;

	/**
	 * 创建时间
	 */
	private Date createtime;


	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getUnionName() {
		return unionName;
	}

	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public List<UnionPreferentialItem> getItems() {
		return items;
	}

	public void setItems(List<UnionPreferentialItem> items) {
		this.items = items;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
}
