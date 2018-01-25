package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.union.main.entity.UnionMain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author linweicong
 * @version 2018-01-25 10:53:52
 */
@ApiModel(value = "联盟折扣卡领卡统计VO")
public class DiscountCardStatisticsVO {
    @ApiModelProperty(value = "联盟")
    private UnionMain union;

    @ApiModelProperty(value = "节点列表")
    private List<DiscountCardStatisticsSpot> spotList;

    public UnionMain getUnion() {
        return union;
    }

    public void setUnion(UnionMain union) {
        this.union = union;
    }

    public List<DiscountCardStatisticsSpot> getSpotList() {
        return spotList;
    }

    public void setSpotList(List<DiscountCardStatisticsSpot> spotList) {
        this.spotList = spotList;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
