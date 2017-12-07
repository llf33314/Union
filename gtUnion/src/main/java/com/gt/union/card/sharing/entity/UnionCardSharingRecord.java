package com.gt.union.card.sharing.entity;

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
 * 联盟卡售卡分成记录
 *
 * @author linweicong
 * @version 2017-11-30 15:23:49
 */
@ApiModel(value = "联盟卡售卡分成记录")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_card_sharing_record")
public class UnionCardSharingRecord extends Model<UnionCardSharingRecord> {
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
     * 售卡价格
     */
    @ApiModelProperty(value = "售卡价格")
    @TableField("sell_price")
    private Double sellPrice;

    /**
     * 分成比例
     */
    @ApiModelProperty(value = "分成比例")
    @TableField("sharing_ratio")
    private Double sharingRatio;

    /**
     * 售卡分成
     */
    @ApiModelProperty(value = "售卡分成")
    @TableField("sharing_money")
    private Double sharingMoney;

    /**
     * 分成盟员id
     */
    @ApiModelProperty(value = "分成盟员id")
    @TableField("sharing_member_id")
    private Integer sharingMemberId;

    /**
     * 售卡盟员id
     */
    @ApiModelProperty(value = "售卡盟员id")
    @TableField("from_member_id")
    private Integer fromMemberId;

    /**
     * 联盟id
     */
    @ApiModelProperty(value = "联盟id")
    @TableField("union_id")
    private Integer unionId;

    /**
     * 活动id
     */
    @ApiModelProperty(value = "活动id")
    @TableField("activity_id")
    private Integer activityId;

    /**
     * 联盟卡id
     */
    @ApiModelProperty(value = "联盟卡id")
    @TableField("card_id")
    private Integer cardId;

    /**
     * 联盟卡粉丝id
     */
    @ApiModelProperty(value = "联盟卡粉丝id")
    @TableField("fan_id")
    private Integer fanId;


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

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Double getSharingRatio() {
        return sharingRatio;
    }

    public void setSharingRatio(Double sharingRatio) {
        this.sharingRatio = sharingRatio;
    }

    public Double getSharingMoney() {
        return sharingMoney;
    }

    public void setSharingMoney(Double sharingMoney) {
        this.sharingMoney = sharingMoney;
    }

    public Integer getSharingMemberId() {
        return sharingMemberId;
    }

    public void setSharingMemberId(Integer sharingMemberId) {
        this.sharingMemberId = sharingMemberId;
    }

    public Integer getFromMemberId() {
        return fromMemberId;
    }

    public void setFromMemberId(Integer fromMemberId) {
        this.fromMemberId = fromMemberId;
    }

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Integer getFanId() {
        return fanId;
    }

    public void setFanId(Integer fanId) {
        this.fanId = fanId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}