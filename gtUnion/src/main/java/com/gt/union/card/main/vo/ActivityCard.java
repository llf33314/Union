package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.main.entity.UnionCard;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author linweicong
 * @version 2018-01-25 08:59:20
 */
@ApiModel(value = "活动卡")
public class ActivityCard {
    @ApiModelProperty(value = "活动卡对象")
    private UnionCard card;

    @ApiModelProperty(value = "是否已过期")
    private Integer isExpired;

    @ApiModelProperty(value = "活动对象")
    private UnionCardActivity activity;

    @ApiModelProperty(value = "优惠项目总数")
    private Integer projectItemCount;

    @ApiModelProperty(value = "活动卡优惠项目列表")
    private List<ActivityCardProject> cardProjectList;

    public UnionCard getCard() {
        return card;
    }

    public void setCard(UnionCard card) {
        this.card = card;
    }

    public Integer getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(Integer isExpired) {
        this.isExpired = isExpired;
    }

    public UnionCardActivity getActivity() {
        return activity;
    }

    public void setActivity(UnionCardActivity activity) {
        this.activity = activity;
    }

    public Integer getProjectItemCount() {
        return projectItemCount;
    }

    public void setProjectItemCount(Integer projectItemCount) {
        this.projectItemCount = projectItemCount;
    }

    public List<ActivityCardProject> getCardProjectList() {
        return cardProjectList;
    }

    public void setCardProjectList(List<ActivityCardProject> cardProjectList) {
        this.cardProjectList = cardProjectList;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
