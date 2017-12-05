package com.gt.union.card.main.entity;

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
 * 联盟卡
 *
 * @author linweicong
 * @version 2017-11-30 15:21:13
 */
@ApiModel(value = "联盟卡")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_card")
public class UnionCard extends Model<UnionCard> {
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
     * 类型(1:折扣卡 2:活动卡)
     */
    @ApiModelProperty(value = "类型(1:折扣卡 2:活动卡)")
    @TableField("type")
    private Integer type;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @TableField("name")
    private String name;
    
    /**
     * 有效期
     */
    @ApiModelProperty(value = "有效期")
    @TableField("validity")
    private Date validity;

    /**
     * 积分
     */
    @ApiModelProperty(value = "积分")
    @TableField("integral")
    private Double integral;

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

    /**
     * 联盟卡粉丝id
     */
    @ApiModelProperty(value = "联盟卡粉丝id")
    @TableField("fan_id")
    private Integer fanId;

    /**
     * 活动id
     */
    @ApiModelProperty(value = "活动id")
    @TableField("activity_id")
    private Integer activityId;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    public Double getIntegral() {
        return integral;
    }

    public void setIntegral(Double integral) {
        this.integral = integral;
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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}