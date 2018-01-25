package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.union.main.entity.UnionMain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author linweicong
 * @version 2018-01-25 10:58:09
 */
@ApiModel(value = "联盟活动卡发售统计VO")
public class ActivityCardStatisticsVO {
    @ApiModelProperty(value = "联盟")
    private UnionMain union;

    @ApiModelProperty(value = "历史发布张数")
    private Integer publishCount;

    @ApiModelProperty(value = "累计售出")
    private Integer sellCount;

    @ApiModelProperty(value = "售卡佣金累计")
    private Double sharingSum;

    public UnionMain getUnion() {
        return union;
    }

    public void setUnion(UnionMain union) {
        this.union = union;
    }

    public Integer getPublishCount() {
        return publishCount;
    }

    public void setPublishCount(Integer publishCount) {
        this.publishCount = publishCount;
    }

    public Integer getSellCount() {
        return sellCount;
    }

    public void setSellCount(Integer sellCount) {
        this.sellCount = sellCount;
    }

    public Double getSharingSum() {
        return sharingSum;
    }

    public void setSharingSum(Double sharingSum) {
        this.sharingSum = sharingSum;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
