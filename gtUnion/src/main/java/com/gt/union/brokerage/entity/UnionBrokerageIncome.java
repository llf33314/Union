package com.gt.union.brokerage.entity;

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
 * @version 2017-10-23 15:28:54
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
     * 创建时间
     */
    private Date createtime;
    /**
     * 删除状态（0：未删除 1：删除）
     */
    @TableField("del_status")
    private Integer delStatus;
    /**
     * 类型（1：售卡 2：商机）
     */
    private Integer type;
    /**
     * 商家id
     */
    @TableField("bus_id")
    private Integer busId;
    /**
     * 佣金金额
     */
    private Double money;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
