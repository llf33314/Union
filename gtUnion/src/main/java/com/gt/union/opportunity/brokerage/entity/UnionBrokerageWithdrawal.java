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
 * 佣金提现
 *
 * @author linweicong
 * @version 2017-11-30 15:25:48
 */
@ApiModel(value = "佣金提现")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_brokerage_withdrawal")
public class UnionBrokerageWithdrawal extends Model<UnionBrokerageWithdrawal> {
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
     * 提现时间
     */
    @ApiModelProperty(value = "提现时间")
    @TableField("create_time")
    private Date createTime;

    /**
     * 提现商家id
     */
    @ApiModelProperty(value = "提现商家id")
    @TableField("bus_id")
    private Integer busId;

    /**
     * 提现金额
     */
    @ApiModelProperty(value = "提现金额")
    @TableField("money")
    private Double money;

    /**
     * 系统订单号
     */
    @ApiModelProperty(value = "系统订单号")
    @TableField("sys_order_no")
    private String sysOrderNo;

    /**
     * 平台管理者id
     */
    @ApiModelProperty(value = "平台管理者id")
    @TableField("verifier_id")
    private Integer verifierId;

    /**
     * 平台管理者名称
     */
    @ApiModelProperty(value = "平台管理者名称")
    @TableField("verifier_name")
    private String verifierName;

    @ApiModelProperty(value = "提现人openid")
    @TableField("openid")
    private String openid;

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

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getSysOrderNo() {
        return sysOrderNo;
    }

    public void setSysOrderNo(String sysOrderNo) {
        this.sysOrderNo = sysOrderNo;
    }

    public Integer getVerifierId() {
        return verifierId;
    }

    public void setVerifierId(Integer verifierId) {
        this.verifierId = verifierId;
    }

    public String getVerifierName() {
        return verifierName;
    }

    public void setVerifierName(String verifierName) {
        this.verifierName = verifierName;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
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