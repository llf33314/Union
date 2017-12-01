package com.gt.union.h5.brokerage.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 未支付的商机佣金
 *
 * @author linweicong
 * @version 2017-12-01 14:49:00
 */
@ApiModel(value = "未支付的商机佣金VO")
public class UnPaidOpportunityBrokerageVO {
    @ApiModelProperty(value = "未支付的商机佣金总额")
    private Double unPaidOpportunityBrokerage;

    @ApiModelProperty(value = "未支付的商机佣金明细")
    private List<OpportunityBrokerage> unPaidOpportunityBrokerageList;

    public Double getUnPaidOpportunityBrokerage() {
        return unPaidOpportunityBrokerage;
    }

    public void setUnPaidOpportunityBrokerage(Double unPaidOpportunityBrokerage) {
        this.unPaidOpportunityBrokerage = unPaidOpportunityBrokerage;
    }

    public List<OpportunityBrokerage> getUnPaidOpportunityBrokerageList() {
        return unPaidOpportunityBrokerageList;
    }

    public void setUnPaidOpportunityBrokerageList(List<OpportunityBrokerage> unPaidOpportunityBrokerageList) {
        this.unPaidOpportunityBrokerageList = unPaidOpportunityBrokerageList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
