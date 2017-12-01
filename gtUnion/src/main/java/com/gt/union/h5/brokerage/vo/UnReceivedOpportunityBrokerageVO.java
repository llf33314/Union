package com.gt.union.h5.brokerage.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 未收的商机佣金
 *
 * @author linweicong
 * @version 2017-12-01 15:01:19
 */
@ApiModel(value = "未收的商机佣金VO")
public class UnReceivedOpportunityBrokerageVO {
    @ApiModelProperty(value = "未收的商机佣金总额")
    private Double unReceivedOpportunityBrokerage;

    @ApiModelProperty(value = "未收的商机佣金明细")
    private List<OpportunityBrokerage> unReceivedOpportunityBrokerageList;

    public Double getUnReceivedOpportunityBrokerage() {
        return unReceivedOpportunityBrokerage;
    }

    public void setUnReceivedOpportunityBrokerage(Double unReceivedOpportunityBrokerage) {
        this.unReceivedOpportunityBrokerage = unReceivedOpportunityBrokerage;
    }

    public List<OpportunityBrokerage> getUnReceivedOpportunityBrokerageList() {
        return unReceivedOpportunityBrokerageList;
    }

    public void setUnReceivedOpportunityBrokerageList(List<OpportunityBrokerage> unReceivedOpportunityBrokerageList) {
        this.unReceivedOpportunityBrokerageList = unReceivedOpportunityBrokerageList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
