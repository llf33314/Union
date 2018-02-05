package com.gt.union.refund.card.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
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
@TableName("t_union_refund_card")
public class UnionRefundCard extends Model<UnionRefundCard> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id")
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
     * 联盟卡粉丝id
     */
    @ApiModelProperty(value = "联盟卡粉丝id")
    @TableField("fan_id")
    private Integer fanId;

    /**
     * 活动卡id
     */
    @ApiModelProperty(value = "活动卡id")
    @TableField("activity_id")
    private Integer activityId;

    /**
     * 退款订单id
     */
    @ApiModelProperty(value = "退款订单id")
    @TableField("refund_order_id")
    private Integer refundOrderId;


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

    public Integer getFanId() {
        return fanId;
    }

    public void setFanId(Integer fanId) {
        this.fanId = fanId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getRefundOrderId() {
        return refundOrderId;
    }

    public void setRefundOrderId(Integer refundOrderId) {
        this.refundOrderId = refundOrderId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}