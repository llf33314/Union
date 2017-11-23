package com.gt.union.card.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 联盟卡购买记录
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@TableName("t_union_card_apply")
public class UnionCardApply extends Model<UnionCardApply> {
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
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

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
     * 订单描述
     */
    @TableField("order_desc")
    private String orderDesc;

    /**
     * 支付类型(1:微信支付 2:支付宝支付)
     */
    @TableField("pay_type")
    private Integer payType;

    /**
     * 支付状态(1:未支付 2:支付成功 3:支付失败 4:已退款)
     */
    @TableField("pay_status")
    private Integer payStatus;

    /**
     * 支付金额
     */
    @TableField("pay_money")
    private Double payMoney;

    /**
     * 联盟卡id
     */
    @TableField("card_id")
    private Integer cardId;

    /**
     * 根id
     */
    @TableField("root_id")
    private Integer rootId;


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

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Double getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Double payMoney) {
        this.payMoney = payMoney;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Integer getRootId() {
        return rootId;
    }

    public void setRootId(Integer rootId) {
        this.rootId = rootId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}