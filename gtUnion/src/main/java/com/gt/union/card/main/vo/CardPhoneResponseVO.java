package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.card.main.entity.UnionCardFan;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author linweicong
 * @version 2018-01-25 09:53:48
 */
@ApiModel(value = "联盟折扣卡和活动卡已办理或将办理VO")
public class CardPhoneResponseVO {
    @ApiModelProperty(value = "粉丝对象")
    private UnionCardFan fan;

    @ApiModelProperty(value = "折扣卡对应的联盟id列表")
    private List<Integer> unionIdList;

    @ApiModelProperty(value = "活动卡对应的活动id列表")
    private List<Integer> activityIdList;

    public UnionCardFan getFan() {
        return fan;
    }

    public void setFan(UnionCardFan fan) {
        this.fan = fan;
    }

    public List<Integer> getUnionIdList() {
        return unionIdList;
    }

    public void setUnionIdList(List<Integer> unionIdList) {
        this.unionIdList = unionIdList;
    }

    public List<Integer> getActivityIdList() {
        return activityIdList;
    }

    public void setActivityIdList(List<Integer> activityIdList) {
        this.activityIdList = activityIdList;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
