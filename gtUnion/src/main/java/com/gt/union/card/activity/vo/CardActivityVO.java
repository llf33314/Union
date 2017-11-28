package com.gt.union.card.activity.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.project.entity.UnionCardProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 活动卡
 *
 * @author linweicong
 * @version 2017-11-25 17:41:05
 */
@ApiModel(value = "活动卡VO")
public class CardActivityVO {
    @ApiModelProperty(value = "活动状态(1:未开始 2:报名中 3:售卖中 4:已结束)")
    private Integer activityStatus;

    @ApiModelProperty(value = "参与盟员数")
    private Integer joinMemberCount;

    @ApiModelProperty(value = "待审核项目数")
    private Integer projectCheckCount;

    @ApiModelProperty(value = "已售活动卡数")
    private Integer cardSellCount;

    @ApiModelProperty(value = "联盟卡活动")
    private UnionCardActivity activity;

    @ApiModelProperty(value = "活动项目")
    private UnionCardProject project;

    public Integer getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(Integer activityStatus) {
        this.activityStatus = activityStatus;
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

    public UnionCardActivity getActivity() {
        return activity;
    }

    public void setActivity(UnionCardActivity activity) {
        this.activity = activity;
    }

    public UnionCardProject getProject() {
        return project;
    }

    public void setProject(UnionCardProject project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
