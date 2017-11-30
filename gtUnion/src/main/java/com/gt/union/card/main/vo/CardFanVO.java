package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.card.main.entity.UnionCardFan;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 首页-联盟卡
 *
 * @author linweicong
 * @version 2017-11-27 11:21:49
 */
@ApiModel(value = "首页-联盟卡VO")
public class CardFanVO {
    @ApiModelProperty(value = "联盟卡粉丝信息")
    private UnionCardFan fan;

    @ApiModelProperty(value = "联盟卡积分")
    private Double integral;

    public UnionCardFan getFan() {
        return fan;
    }

    public void setFan(UnionCardFan fan) {
        this.fan = fan;
    }

    public Double getIntegral() {
        return integral;
    }

    public void setIntegral(Double integral) {
        this.integral = integral;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
