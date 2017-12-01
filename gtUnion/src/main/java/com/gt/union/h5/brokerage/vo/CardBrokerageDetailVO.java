package com.gt.union.h5.brokerage.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 售卡分成佣金明细
 *
 * @author linweicong
 * @version 2017-12-01 14:40:22
 */
@ApiModel(value = "售卡分成佣金明细VO")
public class CardBrokerageDetailVO {
    @ApiModelProperty(value = "售卡佣金总额")
    private Double cardBrokerage;

    @ApiModelProperty(value = "售卡佣金明细")
    private List<CardBrokerage> cardBrokerageList;

    public Double getCardBrokerage() {
        return cardBrokerage;
    }

    public void setCardBrokerage(Double cardBrokerage) {
        this.cardBrokerage = cardBrokerage;
    }

    public List<CardBrokerage> getCardBrokerageList() {
        return cardBrokerageList;
    }

    public void setCardBrokerageList(List<CardBrokerage> cardBrokerageList) {
        this.cardBrokerageList = cardBrokerageList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
