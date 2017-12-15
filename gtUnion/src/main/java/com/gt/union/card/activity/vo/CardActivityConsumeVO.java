package com.gt.union.card.activity.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.main.entity.UnionCard;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 消费核销-活动卡
 *
 * @author linweicong
 * @version 2017-11-30 14:04:41
 */
@ApiModel(value = "消费核销-活动卡VO")
public class CardActivityConsumeVO {
    @ApiModelProperty(value = "联盟卡活动")
    private UnionCardActivity activity;

    @ApiModelProperty(value = "活动卡")
    private UnionCard activityCard;

    public UnionCardActivity getActivity() {
        return activity;
    }

    public void setActivity(UnionCardActivity activity) {
        this.activity = activity;
    }

    public UnionCard getActivityCard() {
        return activityCard;
    }

    public void setActivityCard(UnionCard activityCard) {
        this.activityCard = activityCard;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }
}
