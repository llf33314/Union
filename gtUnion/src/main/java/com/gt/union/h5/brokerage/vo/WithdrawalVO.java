package com.gt.union.h5.brokerage.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 我要提现
 *
 * @author linweicong
 * @version 2017-12-01 14:04:41
 */
@ApiModel(value = "我要提现VO")
public class WithdrawalVO {
    @ApiModelProperty(value = "可提佣金总额")
    private Double availableBrokerage;

    @ApiModelProperty(value = "已支付的商机推荐佣金总额")
    private Double paidOpportunityBrokerage;

    @ApiModelProperty(value = "未支付的商机推荐佣金总额")
    private Double unPaidOpportunityBrokerage;

    @ApiModelProperty(value = "售卡佣金总额")
    private Double cardBrokerage;

    @ApiModelProperty(value = "历史佣金提现总额")
    private Double historyWithdrawal;

    public Double getAvailableBrokerage() {
        return availableBrokerage;
    }

    public void setAvailableBrokerage(Double availableBrokerage) {
        this.availableBrokerage = availableBrokerage;
    }

    public Double getPaidOpportunityBrokerage() {
        return paidOpportunityBrokerage;
    }

    public void setPaidOpportunityBrokerage(Double paidOpportunityBrokerage) {
        this.paidOpportunityBrokerage = paidOpportunityBrokerage;
    }

    public Double getUnPaidOpportunityBrokerage() {
        return unPaidOpportunityBrokerage;
    }

    public void setUnPaidOpportunityBrokerage(Double unPaidOpportunityBrokerage) {
        this.unPaidOpportunityBrokerage = unPaidOpportunityBrokerage;
    }

    public Double getCardBrokerage() {
        return cardBrokerage;
    }

    public void setCardBrokerage(Double cardBrokerage) {
        this.cardBrokerage = cardBrokerage;
    }

    public Double getHistoryWithdrawal() {
        return historyWithdrawal;
    }

    public void setHistoryWithdrawal(Double historyWithdrawal) {
        this.historyWithdrawal = historyWithdrawal;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
