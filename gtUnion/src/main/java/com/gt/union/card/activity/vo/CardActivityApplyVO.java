package com.gt.union.card.activity.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.card.activity.entity.UnionCardActivity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 办理联盟卡-查询联盟卡活动
 *
 * @author linweicong
 * @version 2017-11-30 14:44:39
 */
@ApiModel(value = "办理联盟卡-查询联盟卡活动VO")
public class CardActivityApplyVO {
    @ApiModelProperty(value = "联盟卡活动")
    private UnionCardActivity activity;

    @ApiModelProperty(value = "服务项目数")
    private Integer itemCount;

    public UnionCardActivity getActivity() {
        return activity;
    }

    public void setActivity(UnionCardActivity activity) {
        this.activity = activity;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
