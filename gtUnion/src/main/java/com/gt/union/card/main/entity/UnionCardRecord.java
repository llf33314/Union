package com.gt.union.card.main.entity;

import com.alibaba.fastjson.JSON;
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
 * 联盟卡购买记录
 *
 * @author linweicong
 * @version 2017-11-30 15:21:13
 */
@ApiModel(value = "联盟卡购买记录")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_card_record")
public class UnionCardRecord extends Model<UnionCardRecord> {
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
     * 支付类型(1:微信支付 2:支付宝支付)
     */
    @ApiModelProperty(value = "支付类型(1:微信支付 2:支付宝支付)")
    @TableField("pay_type")
    private Integer payType;

    /**
     * 支付状态(1:未支付 2:支付成功 3:支付失败 4:已退款)
     */
    @ApiModelProperty(value = "支付状态(1:未支付 2:支付成功 3:支付失败 4:已退款)")
    @TableField("pay_status")
    private Integer payStatus;

    /**
     * 支付金额
     */
    @ApiModelProperty(value = "支付金额")
    @TableField("pay_money")
    private Double payMoney;

    /**
     * 活动id
     */
    @ApiModelProperty(value = "活动id")
    @TableField("activity_id")
    private Integer activityId;

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

    public String getAlipayOrderNo() {
        return alipayOrderNo;
    }

    public void setAlipayOrderNo(String alipayOrderNo) {
        this.alipayOrderNo = alipayOrderNo;
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

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}