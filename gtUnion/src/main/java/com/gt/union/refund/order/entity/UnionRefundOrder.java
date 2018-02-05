package com.gt.union.refund.order.entity;

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
 * @author hongjiye
 * @version 2018-02-02 16:58:00
 */
@ApiModel(value = "")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_refund_order")
public class UnionRefundOrder extends Model<UnionRefundOrder> {
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
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @TableField("modify_time")
    private Date modifyTime;

    /**
     * 支付订单号
     */
    @ApiModelProperty(value = "支付订单号")
    @TableField("sys_order_no")
    private String sysOrderNo;

    /**
     * 退款订单号
     */
    @ApiModelProperty(value = "退款订单号")
    @TableField("refund_order_no")
    private String refundOrderNo;

    @ApiModelProperty(value = "订单总金额")
    @TableField("total_money")
    private Double totalMoney;

    /**
     * 退款金额
     */
    @ApiModelProperty(value = "退款金额")
    @TableField("refund_money")
    private Double refundMoney;

    /**
     * 退款类型 1：商机 2：联盟卡
     */
    @ApiModelProperty(value = "退款类型 1：商机 2：联盟卡")
    @TableField("type")
    private Integer type;

    /**
     * 退款状态 1：申请退款 2：退款成功 3：退款失败
     */
    @ApiModelProperty(value = "退款状态 1：申请退款 2：退款成功 3：退款失败")
    @TableField("status")
    private Integer status;

    /**
     * 退款描述
     */
    @ApiModelProperty(value = "退款描述")
    @TableField("desc")
    private String desc;


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

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getSysOrderNo() {
        return sysOrderNo;
    }

    public void setSysOrderNo(String sysOrderNo) {
        this.sysOrderNo = sysOrderNo;
    }

    public String getRefundOrderNo() {
        return refundOrderNo;
    }

    public void setRefundOrderNo(String refundOrderNo) {
        this.refundOrderNo = refundOrderNo;
    }

    public Double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Double getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(Double refundMoney) {
        this.refundMoney = refundMoney;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}