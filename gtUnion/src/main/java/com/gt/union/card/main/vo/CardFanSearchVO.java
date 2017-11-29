package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.card.main.entity.UnionCardFan;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 联盟卡卡号搜索
 *
 * @author linweicong
 * @version 2017-11-27 16:44:02
 */
@ApiModel(value = "联盟卡卡号搜索VO")
public class CardFanSearchVO {
    @ApiModelProperty(value = "联盟卡粉丝信息")
    private UnionCardFan fan;

    @ApiModelProperty(value = "联盟积分")
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
