package com.gt.union.card.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.card.entity.UnionCardActivity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 活动项目
 *
 * @author linweicong
 * @version 2017-11-24 17:20:24
 */
@ApiModel(value = "活动项目")
public class ActivityProjectIndexVO {
    @ApiModelProperty(value = "活动对象")
    private UnionCardActivity activity;

    @ApiModelProperty(value = "活动项目id")
    private Integer activityProjectId;

    @ApiModelProperty(value = "活动状态(1:未开始 2:报名中 3:售卡中 4:已结束)")
    private Integer activityStatus;

    @ApiModelProperty(value = "是否盟主(0:否 1:是)")
    private Integer isUnionOwner;

    @ApiModelProperty(value = "参与盟员数")
    private Integer joinMemberCount;

    @ApiModelProperty(value = "待审核项目数")
    private Integer projectCheckCount;

    @ApiModelProperty(value = "已售活动卡数")
    private Integer cardSellCount;

    public UnionCardActivity getActivity() {
        return activity;
    }

    public void setActivity(UnionCardActivity activity) {
        this.activity = activity;
    }

    public Integer getActivityProjectId() {
        return activityProjectId;
    }

    public void setActivityProjectId(Integer activityProjectId) {
        this.activityProjectId = activityProjectId;
    }

    public Integer getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(Integer activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Integer getIsUnionOwner() {
        return isUnionOwner;
    }

    public void setIsUnionOwner(Integer isUnionOwner) {
        this.isUnionOwner = isUnionOwner;
    }

    public Integer getJoinMemberCount() {
        return joinMemberCount;
    }

    public void setJoinMemberCount(Integer joinMemberCount) {
        this.joinMemberCount = joinMemberCount;
    }

    public Integer getProjectCheckCount() {
        return projectCheckCount;
    }

    public void setProjectCheckCount(Integer projectCheckCount) {
        this.projectCheckCount = projectCheckCount;
    }

    public Integer getCardSellCount() {
        return cardSellCount;
    }

    public void setCardSellCount(Integer cardSellCount) {
        this.cardSellCount = cardSellCount;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
