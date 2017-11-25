package com.gt.union.opportunity.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 佣金收入
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@TableName("t_union_brokerage_income")
public class UnionBrokerageIncome extends Model<UnionBrokerageIncome> {
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
     * 类型(1:售卡 2:商机)
     */
    @TableField("type")
    private Integer type;

    /**
     * 佣金金额
     */
    @TableField("money")
    private Double money;

    /**
     * 收入商家id
     */
    @TableField("bus_id")
    private Integer busId;

    /**
     * 收入盟员id
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
     * 商机id
     */
    @TableField("opportunity_id")
    private Integer opportunityId;


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

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
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

    public Integer getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Integer opportunityId) {
        this.opportunityId = opportunityId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}