package com.gt.union.card.activity.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.card.activity.entity.UnionCardActivity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 联盟卡活动状态
 *
 * @author linweicong
 * @version 2017-11-28 14:14:07
 */
@ApiModel(value = "联盟卡活动状态VO")
public class CardActivityStatusVO {
    @ApiModelProperty(value = "活动状态(1:未开始 2:报名中 3:售卖中 4:已结束)")
    private Integer activityStatus;

    @ApiModelProperty(value = "联盟卡活动")
    private UnionCardActivity activity;

    public Integer getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(Integer activityStatus) {
        this.activityStatus = activityStatus;
    }

    public UnionCardActivity getActivity() {
        return activity;
    }

    public void setActivity(UnionCardActivity activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
