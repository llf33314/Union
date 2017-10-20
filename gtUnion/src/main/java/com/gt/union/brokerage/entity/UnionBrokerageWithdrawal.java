package com.gt.union.brokerage.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 佣金提现记录
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
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
     * 提现时间
     */
    private Date createtime;
    /**
     * 删除状态（0：未删除 1：删除）
     */
    @TableField("del_status")
    private Integer delStatus;
    /**
     * 提现商家id
     */
    @TableField("bus_id")
    private Integer busId;
    /**
     * 提现金额
     */
    private Double money;
    /**
     * 佣金管理平台用户id
     */
    @TableField("verifier_id")
    private Integer verifierId;

    /**
     * 用户粉丝id
     */
    @TableField("third_member_id")
    private Integer thirdMemberId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
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

    public Integer getThirdMemberId() {
        return thirdMemberId;
    }

    public void setThirdMemberId(Integer thirdMemberId) {
        this.thirdMemberId = thirdMemberId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
