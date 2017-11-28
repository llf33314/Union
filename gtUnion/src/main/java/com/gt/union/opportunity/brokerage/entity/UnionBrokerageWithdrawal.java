package com.gt.union.opportunity.brokerage.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 佣金提现
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@TableName("t_union_brokerage_withdrawal")
public class UnionBrokerageWithdrawal extends Model<UnionBrokerageWithdrawal> {
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
     * 提现时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 提现商家id
     */
    @TableField("bus_id")
    private Integer busId;

    /**
     * 提现金额
     */
    @TableField("money")
    private Double money;

    /**
     * 平台管理者id
     */
    @TableField("verifier_id")
    private Integer verifierId;

    /**
     * 平台管理者名称
     */
    @TableField("verifier_name")
    private String verifierName;


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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}