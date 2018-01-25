package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 首页-联盟卡-详情
 *
 * @author linweicong
 * @version 2017-11-27 11:29:12
 */
@ApiModel(value = "首页-联盟卡-详情VO")
public class CardFanDetailVO {
    @ApiModelProperty(value = "联盟折扣卡")
    private DiscountCard discountCard;

    @ApiModelProperty(value = "活动卡列表")
    private List<ActivityCard> activityCardList;

    public DiscountCard getDiscountCard() {
        return discountCard;
    }

    public void setDiscountCard(DiscountCard discountCard) {
        this.discountCard = discountCard;
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
