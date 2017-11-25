package com.gt.union.card.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 消费核销
 *
 * @author linweicong
 * @version 2017-11-25 10:51:42
 */
@TableName("t_union_consume")
public class UnionConsume extends Model<UnionConsume> {
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
     * 消费类型(1:线上 2:线下)
     */
    @TableField("type")
    private Integer type;

    /**
     * 消费总额
     */
    @TableField("consume_money")
    private Double consumeMoney;

    /**
     * 支付金额
     */
    @TableField("pay_money")
    private Double payMoney;

    /**
     * 支付方式(0:现金 1:微信 2:支付宝)
     */
    @TableField("pay_type")
    private Integer payType;

    /**
     * 支付状态(1:未支付 2:已支付 3:已退款)
     */
    @TableField("pay_status")
    private Integer payStatus;

    /**
     * 使用积分额
     */
    @TableField("use_integral")
    private Integer useIntegral;

    /**
     * 赠送积分额
     */
    @TableField("give_integral")
    private Integer giveIntegral;

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
     * 消费行业类型(0:线下 >0:其他行业)
     */
    @TableField("business_type")
    private Integer businessType;

    /**
     * 行业消费描述
     */
    @TableField("business_desc")
    private String businessDesc;

    /**
     * 行业订单id
     */
    @TableField("business_order_id")
    private Integer businessOrderId;

    /**
     * 门店id
     */
    @TableField("shop_id")
    private Integer shopId;

    /**
     * 盟员id
     */
    @TableField("member_id")
    private Integer memberId;

    /**
     * 联盟id
     */
    @TableField("union_id")
    private Integer unionId;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getUseIntegral() {
        return useIntegral;
    }

    public void setUseIntegral(Integer useIntegral) {
        this.useIntegral = useIntegral;
    }

    public Integer getGiveIntegral() {
        return giveIntegral;
    }

    public void setGiveIntegral(Integer giveIntegral) {
        this.giveIntegral = giveIntegral;
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

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getBusinessDesc() {
        return businessDesc;
    }

    public void setBusinessDesc(String businessDesc) {
        this.businessDesc = businessDesc;
    }

    public Integer getBusinessOrderId() {
        return businessOrderId;
    }

    public void setBusinessOrderId(Integer businessOrderId) {
        this.businessOrderId = businessOrderId;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
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