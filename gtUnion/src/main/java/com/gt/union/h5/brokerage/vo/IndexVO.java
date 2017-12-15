package com.gt.union.h5.brokerage.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * h5佣金平台首页
 *
 * @author linweicong
 * @version 2017-12-01 11:56:10
 */
@ApiModel(value = "h5佣金平台首页VO")
public class IndexVO {
    @ApiModelProperty(value = "可提现佣金总额")
    private Double availableBrokerage;

    @ApiModelProperty(value = "未支付商机佣金总额")
    private Double unPaidOpportunityBrokerage;

    @ApiModelProperty(value = "历史佣金总额(售卡+佣金(已+未))")
    private Double brokerageSum;

    public Double getAvailableBrokerage() {
        return availableBrokerage;
    }

    public void setAvailableBrokerage(Double availableBrokerage) {
        this.availableBrokerage = availableBrokerage;
    }

    public Double getUnPaidOpportunityBrokerage() {
        return unPaidOpportunityBrokerage;
    }

    public void setUnPaidOpportunityBrokerage(Double unPaidOpportunityBrokerage) {
        this.unPaidOpportunityBrokerage = unPaidOpportunityBrokerage;
    }

    public Double getBrokerageSum() {
        return brokerageSum;
    }

    public void setBrokerageSum(Double brokerageSum) {
        this.brokerageSum = brokerageSum;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }
}
