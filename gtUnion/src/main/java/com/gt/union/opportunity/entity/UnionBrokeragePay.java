package com.gt.union.opportunity.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 佣金支出
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@TableName("t_union_brokerage_pay")
public class UnionBrokeragePay extends Model<UnionBrokeragePay> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 是否删除(0:否 1:是)
     */
    @TableField("del_status")
    private Integer delStatus;

    /**
     * 支付时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 支付类型(1:微信 2:支付宝)
     */
    @TableField("type")
    private Integer type;

    /**
     * 支付状态(1:未支付 2:支付成功 3:已退款)
     */
    @TableField("status")
    private Integer status;

    /**
     * 支付金额
     */
    @TableField("money")
    private Double money;

    /**
     * 支付商家id
     */
    @TableField("from_bus_id")
    private Integer fromBusId;

    /**
     * 收款商家id
     */
    @TableField("to_bus_id")
    private Integer toBusId;

    /**
     * 支付盟员id
     */
    @TableField("from_member_id")
    private Integer fromMemberId;

    /**
     * 收款盟员id
     */
    @TableField("to_member_id")
    private Integer toMemberId;

    /**
     * 联盟id
     */
    @TableField("union_id")
    private Integer unionId;

    /**
     * 商机id
     */
    @TableField("opportunity_id")
    private Integer opportunityId;

    /**
     * 平台管理员id
     */
    @TableField("verifier_id")
    private Integer verifierId;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
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

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }

    public Integer getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Integer opportunityId) {
        this.opportunityId = opportunityId;
    }

    public Integer getVerifierId() {
        return verifierId;
    }

    public void setVerifierId(Integer verifierId) {
        this.verifierId = verifierId;
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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}