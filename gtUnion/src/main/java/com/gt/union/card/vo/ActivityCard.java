package com.gt.union.card.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 活动卡
 *
 * @author linweicong
 * @version 2017-11-24 14:37:37
 */
@ApiModel(value = "活动卡")
public class ActivityCard {
    @ApiModelProperty(value = "活动名称")
    private String name;

    @ApiModelProperty(value = "办卡时间")
    private Date createTime;

    @ApiModelProperty(value = "有效时间")
    private Date validity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
