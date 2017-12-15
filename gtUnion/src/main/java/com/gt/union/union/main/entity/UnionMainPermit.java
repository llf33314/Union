package com.gt.union.union.main.entity;

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
 * 联盟许可
 *
 * @author linweicong
 * @version 2017-11-30 15:28:06
 */
@ApiModel(value = "联盟许可")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_main_permit")
public class UnionMainPermit extends Model<UnionMainPermit> {
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
     * 商家id
     */
    @ApiModelProperty(value = "商家id")
    @TableField("bus_id")
    private Integer busId;

    /**
     * 盟主服务套餐id
     */
    @ApiModelProperty(value = "盟主服务套餐id")
    @TableField("package_id")
    private Integer packageId;

    /**
     * 许可有效期
     */
    @ApiModelProperty(value = "许可有效期")
    @TableField("validity")
    private Date validity;

    /**
     * 订单金额
     */
    @ApiModelProperty(value = "订单金额")
    @TableField("order_money")
    private Double orderMoney;

    /**
     * 订单状态(1:未支付 2:已支付 3:支付失败)
     */
    @ApiModelProperty(value = "订单状态(1:未支付 2:已支付 3:支付失败)")
    @TableField("order_status")
    private Integer orderStatus;

    /**
     * 支付方式(1:微信支付 2:支付宝支付)
     */
    @ApiModelProperty(value = "支付方式(1:微信支付 2:支付宝支付)")
    @TableField("pay_type")
    private Integer payType;

    /**
     * 内部订单号
     */
    @ApiModelProperty(value = "内部订单号")
    @TableField("sys_order_no")
    private String sysOrderNo;

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

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    public Double getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(Double orderMoney) {
        this.orderMoney = orderMoney;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getSysOrderNo() {
        return sysOrderNo;
    }

    public void setSysOrderNo(String sysOrderNo) {
        this.sysOrderNo = sysOrderNo;
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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}