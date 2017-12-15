package com.gt.union.opportunity.brokerage.entity;

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
 * 佣金支出
 *
 * @author linweicong
 * @version 2017-11-30 15:25:48
 */
@ApiModel(value = "佣金支出")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_brokerage_pay")
public class UnionBrokeragePay extends Model<UnionBrokeragePay> {
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
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
    @TableField("create_time")
    private Date createTime;

    /**
     * 支付类型(1:微信 2:支付宝)
     */
    @ApiModelProperty(value = "支付类型(1:微信 2:支付宝)")
    @TableField("type")
    private Integer type;

    /**
     * 支付状态(1:未支付 2:支付成功 3:已退款)
     */
    @ApiModelProperty(value = "支付状态(1:未支付 2:支付成功 3:已退款)")
    @TableField("status")
    private Integer status;

    /**
     * 支付金额
     */
    @ApiModelProperty(value = "支付金额")
    @TableField("money")
    private Double money;

    /**
     * 支付商家id
     */
    @ApiModelProperty(value = "支付商家id")
    @TableField("from_bus_id")
    private Integer fromBusId;

    /**
     * 收款商家id
     */
    @ApiModelProperty(value = "收款商家id")
    @TableField("to_bus_id")
    private Integer toBusId;

    /**
     * 支付盟员id
     */
    @ApiModelProperty(value = "支付盟员id")
    @TableField("from_member_id")
    private Integer fromMemberId;

    /**
     * 收款盟员id
     */
    @ApiModelProperty(value = "收款盟员id")
    @TableField("to_member_id")
    private Integer toMemberId;

    /**
     * 联盟id
     */
    @ApiModelProperty(value = "联盟id")
    @TableField("union_id")
    private Integer unionId;

    /**
     * 商机id
     */
    @ApiModelProperty(value = "商机id")
    @TableField("opportunity_id")
    private Integer opportunityId;

    /**
     * 平台管理员id
     */
    @ApiModelProperty(value = "平台管理员id")
    @TableField("verifier_id")
    private Integer verifierId;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    @TableField("order_no")
    private String orderNo;

    /**
     * 微信订单编号
     */
    @ApiModelProperty(value = "微信订单编号")
    @TableField("wx_order_no")
    private String wxOrderNo;

    /**
     * 支付宝订单编号
     */
    @ApiModelProperty(value = "支付宝订单编号")
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