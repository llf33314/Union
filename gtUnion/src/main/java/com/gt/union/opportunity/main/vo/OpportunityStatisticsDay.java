package com.gt.union.opportunity.main.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 商机佣金统计(天)
 *
 * @author linweicong
 * @version 2017-11-25 09:53:24
 */
@ApiModel(value = "商机佣金统计(天)")
public class OpportunityStatisticsDay {
    @ApiModelProperty(value = "已支付收入")
    private Double paidIncome;

    @ApiModelProperty(value = "已支付支出")
    private Double paidExpense;

    public Double getPaidIncome() {
        return paidIncome;
    }

    public void setPaidIncome(Double paidIncome) {
        this.paidIncome = paidIncome;
    }

    public Double getPaidExpense() {
        return paidExpense;
    }

    public void setPaidExpense(Double paidExpense) {
        this.paidExpense = paidExpense;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
