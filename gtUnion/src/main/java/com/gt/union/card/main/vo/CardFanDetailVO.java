package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.card.main.entity.UnionCard;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 联盟卡粉丝详情信息
 *
 * @author linweicong
 * @version 2017-11-27 11:29:12
 */
@ApiModel(value = "联盟卡粉丝详情信息VO")
public class CardFanDetailVO {
    @ApiModelProperty(value = "折扣卡")
    private UnionCard discountCard;
    
    @ApiModelProperty(value = "折扣卡折扣")
    private Double discount;

    @ApiModelProperty(value = "活动卡列表")
    private List<UnionCard> activityCardList;

    public UnionCard getDiscountCard() {
        return discountCard;
    }

    public void setDiscountCard(UnionCard discountCard) {
        this.discountCard = discountCard;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public List<UnionCard> getActivityCardList() {
        return activityCardList;
    }

    public void setActivityCardList(List<UnionCard> activityCardList) {
        this.activityCardList = activityCardList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
