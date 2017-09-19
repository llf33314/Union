package com.gt.union.opportunity.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 商机推荐
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_opportunity")
public class UnionOpportunity extends Model<UnionOpportunity> {

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
     * 推荐类型（1：线上 2：线下）
     */
	private Integer type;
    /**
     * 推荐盟员id
     */
	@TableField("from_member_id")
	private Integer fromMemberId;
    /**
     * 接收盟员id
     */
	@TableField("to_member_id")
	private Integer toMemberId;
    /**
     * 是否受理（1：受理中  2：已接受 3：已拒绝）
     */
	@TableField("is_accept")
	private Integer isAccept;
    /**
     * 受理金额
     */
	@TableField("accept_price")
	private Double acceptPrice;
    /**
     * 佣金金额
     */
	@TableField("brokerage_price")
	private Double brokeragePrice;
    /**
     * 修改时间
     */
	private Date modifytime;
    /**
     * 客户姓名
     */
	@TableField("client_name")
	private String clientName;
    /**
     * 客户电话
     */
	@TableField("client_phone")
	private String clientPhone;
    /**
     * 业务备注
     */
	@TableField("business_msg")
	private String businessMsg;

	@TableField("is_urge_brokerage")
	private Integer isUrgeBrokerage;


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

	public Integer getIsAccept() {
		return isAccept;
	}

	public void setIsAccept(Integer isAccept) {
		this.isAccept = isAccept;
	}

	public Double getAcceptPrice() {
		return acceptPrice;
	}

	public void setAcceptPrice(Double acceptPrice) {
		this.acceptPrice = acceptPrice;
	}

	public Double getBrokeragePrice() {
		return brokeragePrice;
	}

	public void setBrokeragePrice(Double brokeragePrice) {
		this.brokeragePrice = brokeragePrice;
	}

	public Date getModifytime() {
		return modifytime;
	}

	public void setModifytime(Date modifytime) {
		this.modifytime = modifytime;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientPhone() {
		return clientPhone;
	}

	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}

	public String getBusinessMsg() {
		return businessMsg;
	}

	public void setBusinessMsg(String businessMsg) {
		this.businessMsg = businessMsg;
	}

	public Integer getIsUrgeBrokerage() {
		return isUrgeBrokerage;
	}

	public void setIsUrgeBrokerage(Integer isUrgeBrokerage) {
		this.isUrgeBrokerage = isUrgeBrokerage;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
