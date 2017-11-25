package com.gt.union.card.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 联盟卡详情
 *
 * @author linweicong
 * @version 2017-11-24 14:33:40
 */
@ApiModel(value = "联盟卡详情")
public class RootDetailVO {
    @ApiModelProperty(value = "折扣卡办卡时间")
    private Date createTime;

    @ApiModelProperty(value = "折扣卡折扣")
    private Double discount;

    @ApiModelProperty(value = "活动卡列表")
    private List<ActivityCard> activityCardList;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public List<ActivityCard> getActivityCardList() {
        return activityCardList;
    }

    public void setActivityCardList(List<ActivityCard> activityCardList) {
        this.activityCardList = activityCardList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
