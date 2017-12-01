package com.gt.union.h5.brokerage.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 商机推荐佣金明细
 *
 * @author linweicong
 * @version 2017-12-01 14:12:30
 */
@ApiModel(value = "商机推荐佣金明细VO")
public class OpportunityBrokerageDetailVO {
    @ApiModelProperty(value = "已支付的商机佣金总额")
    private Double paidOpportunityBrokerage;

    @ApiModelProperty(value = "已支付的商机佣金明细")
    private List<OpportunityBrokerage> paidOpportunityBrokerageList;

    public Double getPaidOpportunityBrokerage() {
        return paidOpportunityBrokerage;
    }

    public void setPaidOpportunityBrokerage(Double paidOpportunityBrokerage) {
        this.paidOpportunityBrokerage = paidOpportunityBrokerage;
    }

    public List<OpportunityBrokerage> getPaidOpportunityBrokerageList() {
        return paidOpportunityBrokerageList;
    }

    public void setPaidOpportunityBrokerageList(List<OpportunityBrokerage> paidOpportunityBrokerageList) {
        this.paidOpportunityBrokerageList = paidOpportunityBrokerageList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}