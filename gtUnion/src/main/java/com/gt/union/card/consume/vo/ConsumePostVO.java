package com.gt.union.card.consume.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.card.consume.entity.UnionConsume;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 前台-联盟卡消费核销-支付表单
 *
 * @author linweicong
 * @version 2017-12-09 15:11:28
 */
@ApiModel(value = "前台-联盟卡消费核销-支付表单VO")
public class ConsumePostVO {
    @ApiModelProperty(value = "门店id")
    private Integer shopId;

    @ApiModelProperty(value = "消费核销信息")
    private UnionConsume consume;

    @ApiModelProperty(value = "是否使用积分")
    private Integer isUserIntegral;

    @ApiModelProperty(value = "联盟卡活动id")
    private Integer activityId;

    @ApiModelProperty(value = "非ERP文本项目优惠列表")
    private List<UnionCardProjectItem> textList;

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public UnionConsume getConsume() {
        return consume;
    }

    public void setConsume(UnionConsume consume) {
        this.consume = consume;
    }

    public Integer getIsUserIntegral() {
        return isUserIntegral;
    }

    public void setIsUserIntegral(Integer isUserIntegral) {
        this.isUserIntegral = isUserIntegral;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public List<UnionCardProjectItem> getTextList() {
        return textList;
    }

    public void setTextList(List<UnionCardProjectItem> textList) {
        this.textList = textList;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
