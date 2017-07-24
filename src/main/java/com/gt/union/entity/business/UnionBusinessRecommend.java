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
 * 联盟商家商机推荐
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@TableName("t_union_business_recommend")
public class UnionBusinessRecommend extends Model<UnionBusinessRecommend> {

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
     * 推荐商家
     */
	@TableField("from_bus_id")
	private Integer fromBusId;
    /**
     * 接受商家
     */
	@TableField("to_bus_id")
	private Integer toBusId;
    /**
     * 是否受理（0：未处理  1：受理 2：拒绝）
     */
	@TableField("is_acceptance")
	private Integer isAcceptance;
    /**
     * 受理价格
     */
	@TableField("acceptance_price")
	private Double acceptancePrice;
    /**
     * 是否给予佣金（0：未处理 1：给予 2：拒绝）
     */
	@TableField("is_confirm")
	private Integer isConfirm;
    /**
     * 交易时间
     */
	@TableField("confirm_time")
	private Date confirmTime;
    /**
     * 推荐类型（1：线上 2：线下）
     */
	@TableField("recommend_type")
	private Integer recommendType;
    /**
     * 商机佣金金额
     */
	@TableField("business_price")
	private Double businessPrice;
    /**
     * 推荐联盟商家id
     */
	@TableField("from_member_id")
	private Integer fromMemberId;
    /**
     * 接收联盟商家id
     */
	@TableField("to_member_id")
	private Integer toMemberId;
    /**
     * 是否催促佣金（0：未 1：已催促）
     */
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

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Integer getFromBusId() {
		return fromBusId;
	}

	public void setFromBusId(Integer fromBusId) {
		this.fromBusId = fromBusId;
	}

	public Integer getToBusId() {
		return toBusId;
	}

	public void setToBusId(Integer toBusId) {
		this.toBusId = toBusId;
	}

	public Integer getIsAcceptance() {
		return isAcceptance;
	}

	public void setIsAcceptance(Integer isAcceptance) {
		this.isAcceptance = isAcceptance;
	}

	public Double getAcceptancePrice() {
		return acceptancePrice;
	}

	public void setAcceptancePrice(Double acceptancePrice) {
		this.acceptancePrice = acceptancePrice;
	}

	public Integer getIsConfirm() {
		return isConfirm;
	}

	public void setIsConfirm(Integer isConfirm) {
		this.isConfirm = isConfirm;
	}

	public Date getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	public Integer getRecommendType() {
		return recommendType;
	}

	public void setRecommendType(Integer recommendType) {
		this.recommendType = recommendType;
	}

	public Double getBusinessPrice() {
		return businessPrice;
	}

	public void setBusinessPrice(Double businessPrice) {
		this.businessPrice = businessPrice;
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
