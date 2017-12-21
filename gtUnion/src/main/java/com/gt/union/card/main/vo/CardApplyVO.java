package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 消费核销联盟卡
 *
 * @author linweicong
 * @version 2017-11-30 14:30:38
 */
@ApiModel(value = "消费核销联盟卡VO")
public class CardApplyVO {
    @ApiModelProperty(value = "联盟列表")
    private List<UnionMain> unionList;

    @ApiModelProperty(value = "当前选中的联盟")
    private UnionMain currentUnion;

    @ApiModelProperty(value = "当前选中联盟下的盟员身份")
    private UnionMember currentMember;

    @ApiModelProperty(value = "联盟卡活动列表")
    private List<UnionCardActivity> activityList;

    @ApiModelProperty(value = "是否需要展示折扣卡(0:否 1:是)")
    private Integer isDiscountCard;

    public List<UnionMain> getUnionList() {
        return unionList;
    }

    public void setUnionList(List<UnionMain> unionList) {
        this.unionList = unionList;
    }

    public UnionMain getCurrentUnion() {
        return currentUnion;
    }

    public void setCurrentUnion(UnionMain currentUnion) {
        this.currentUnion = currentUnion;
    }

    public UnionMember getCurrentMember() {
        return currentMember;
    }

    public void setCurrentMember(UnionMember currentMember) {
        this.currentMember = currentMember;
    }

    public List<UnionCardActivity> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<UnionCardActivity> activityList) {
        this.activityList = activityList;
    }

    public Integer getIsDiscountCard() {
        return isDiscountCard;
    }

    public void setIsDiscountCard(Integer isDiscountCard) {
        this.isDiscountCard = isDiscountCard;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
