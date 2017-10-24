package com.gt.union.brokerage.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 佣金支付记录
 *
 * @author linweicong
 * @version 2017-10-23 15:28:54
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
     * 支付商家id（需要支付佣金的商家id）
     */
    @TableField("from_bus_id")
    private Integer fromBusId;
    /**
     * 收款商家id（应得佣金的商家id）
     */
    @TableField("to_bus_id")
    private Integer toBusId;
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
     * 商机推荐id
     */
    @TableField("opportunity_id")
    private Integer opportunityId;
    /**
     * 佣金平台管理员id
     */
    @TableField("verifier_id")
    private Integer verifierId;


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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
