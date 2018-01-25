package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 联盟卡办理列表VO
 *
 * @author linweicong
 * @version 2017-11-30 14:30:38
 */
@ApiModel(value = "联盟卡办理列表VO")
public class CardApplyVO {
    @ApiModelProperty(value = "折扣卡列表")
    private List<DiscountCard> discountCardList;

    @ApiModelProperty(value = "活动卡列表")
    private List<ActivityCard> activityCardList;

    public List<DiscountCard> getDiscountCardList() {
        return discountCardList;
    }

    public void setDiscountCardList(List<DiscountCard> discountCardList) {
        this.discountCardList = discountCardList;
    }

    public List<ActivityCard> getActivityCardList() {
        return activityCardList;
    }

    public void setActivityCardList(List<ActivityCard> activityCardList) {
        this.activityCardList = activityCardList;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
