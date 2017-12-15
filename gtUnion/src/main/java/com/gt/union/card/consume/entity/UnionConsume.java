package com.gt.union.card.consume.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 消费核销
 *
 * @author linweicong
 * @version 2017-11-30 15:19:43
 */
@ApiModel(value = "消费核销")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_consume")
public class UnionConsume extends Model<UnionConsume> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 是否删除(0:否 1:是)
     */
    @ApiModelProperty(value = "是否删除(0:否 1:是)")
    @TableField("del_status")
    private Integer delStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    /**
     * 消费类型(1:线上 2:线下)
     */
    @ApiModelProperty(value = "消费类型(1:线上 2:线下)")
    @TableField("type")
    private Integer type;

    /**
     * 消费总额
     */
    @ApiModelProperty(value = "消费总额")
    @TableField("consume_money")
    private Double consumeMoney;

    /**
     * 支付金额
     */
    @ApiModelProperty(value = "支付金额")
    @TableField("pay_money")
    private Double payMoney;

    /**
     * 支付方式(0:现金 1:微信 2:支付宝)
     */
    @ApiModelProperty(value = "支付方式(0:现金 1:微信 2:支付宝)")
    @TableField("pay_type")
    private Integer payType;

    /**
     * 支付状态(1:未支付 2:已支付 3:已退款)
     */
    @ApiModelProperty(value = "支付状态(1:未支付 2:已支付 3:已退款)")
    @TableField("pay_status")
    private Integer payStatus;

    /**
     * 使用积分额
     */
    @ApiModelProperty(value = "使用积分额")
    @TableField("use_integral")
    private Double useIntegral;

    /**
     * 赠送积分额
     */
    @ApiModelProperty(value = "赠送积分额")
    @TableField("give_integral")
    private Double giveIntegral;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    @TableField("order_no")
    private String orderNo;

    /**
     * 微信订单号
     */
    @ApiModelProperty(value = "微信订单号")
    @TableField("wx_order_no")
    private String wxOrderNo;

    /**
     * 支付宝订单号
     */
    @ApiModelProperty(value = "支付宝订单号")
    @TableField("alipay_order_no")
    private String alipayOrderNo;

    /**
     * 消费行业类型(0:线下 >0:其他行业)
     */
    @ApiModelProperty(value = "消费行业类型(0:线下 >0:其他行业)")
    @TableField("business_type")
    private Integer businessType;

    /**
     * 行业消费描述
     */
    @ApiModelProperty(value = "行业消费描述")
    @TableField("business_desc")
    private String businessDesc;

    /**
     * 行业订单id
     */
    @ApiModelProperty(value = "行业订单id")
    @TableField("business_order_id")
    private Integer businessOrderId;

    /**
     * 门店id
     */
    @ApiModelProperty(value = "门店id")
    @TableField("shop_id")
    private Integer shopId;

    /**
     * 盟员id
     */
    @ApiModelProperty(value = "盟员id")
    @TableField("member_id")
    private Integer memberId;

    /**
     * 联盟id
     */
    @ApiModelProperty(value = "联盟id")
    @TableField("union_id")
    private Integer unionId;

    /**
     * 联盟卡id
     */
    @ApiModelProperty(value = "联盟卡id")
    @TableField("card_id")
    private Integer cardId;

    /**
     * 联盟卡粉丝id
     */
    @ApiModelProperty(value = "联盟卡粉丝id")
    @TableField("fan_id")
    private Integer fanId;


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

    public Double getUseIntegral() {
        return useIntegral;
    }

    public void setUseIntegral(Double useIntegral) {
        this.useIntegral = useIntegral;
    }

    public Double getGiveIntegral() {
        return giveIntegral;
    }

    public void setGiveIntegral(Double giveIntegral) {
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

    public String getAlipayOrderNo() {
        return alipayOrderNo;
    }

    public void setAlipayOrderNo(String alipayOrderNo) {
        this.alipayOrderNo = alipayOrderNo;
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

    public Integer getFanId() {
        return fanId;
    }

    public void setFanId(Integer fanId) {
        this.fanId = fanId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }
}