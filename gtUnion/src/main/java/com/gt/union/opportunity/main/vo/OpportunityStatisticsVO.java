package com.gt.union.opportunity.main.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 商机佣金统计
 *
 * @author linweicong
 * @version 2017-11-25 09:48:20
 */
@ApiModel(value = "商机佣金统计")
public class OpportunityStatisticsVO {
    @ApiModelProperty(value = "未支付佣金收入")
    private Double unPaidIncome;

    @ApiModelProperty(value = "已支付佣金收入")
    private Double paidIncome;

    @ApiModelProperty(value = "总收入")
    private Double incomeSum;

    @ApiModelProperty(value = "未支付佣金支出")
    private Double unPaidExpense;

    @ApiModelProperty(value = "已支付佣金支出")
    private Double paidExpense;

    @ApiModelProperty(value = "总支出")
    private Double expenseSum;

    @ApiModelProperty(value = "星期一收支对象")
    private OpportunityStatisticsDay monday;

    @ApiModelProperty(value = "星期二收支对象")
    private OpportunityStatisticsDay tuesday;

    @ApiModelProperty(value = "星期三收支对象")
    private OpportunityStatisticsDay wednesday;

    @ApiModelProperty(value = "星期四收支对象")
    private OpportunityStatisticsDay thursday;

    @ApiModelProperty(value = "星期五收支对象")
    private OpportunityStatisticsDay friday;

    @ApiModelProperty(value = "星期六收支对象")
    private OpportunityStatisticsDay saturday;

    @ApiModelProperty(value = "星期天收支对象")
    private OpportunityStatisticsDay sunday;

    public Double getUnPaidIncome() {
        return unPaidIncome;
    }

    public void setUnPaidIncome(Double unPaidIncome) {
        this.unPaidIncome = unPaidIncome;
    }

    public Double getPaidIncome() {
        return paidIncome;
    }

    public void setPaidIncome(Double paidIncome) {
        this.paidIncome = paidIncome;
    }

    public Double getIncomeSum() {
        return incomeSum;
    }

    public void setIncomeSum(Double incomeSum) {
        this.incomeSum = incomeSum;
    }

    public Double getUnPaidExpense() {
        return unPaidExpense;
    }

    public void setUnPaidExpense(Double unPaidExpense) {
        this.unPaidExpense = unPaidExpense;
    }

    public Double getPaidExpense() {
        return paidExpense;
    }

    public void setPaidExpense(Double paidExpense) {
        this.paidExpense = paidExpense;
    }

    public Double getExpenseSum() {
        return expenseSum;
    }

    public void setExpenseSum(Double expenseSum) {
        this.expenseSum = expenseSum;
    }

    public OpportunityStatisticsDay getMonday() {
        return monday;
    }

    public void setMonday(OpportunityStatisticsDay monday) {
        this.monday = monday;
    }

    public OpportunityStatisticsDay getTuesday() {
        return tuesday;
    }

    public void setTuesday(OpportunityStatisticsDay tuesday) {
        this.tuesday = tuesday;
    }

    public OpportunityStatisticsDay getWednesday() {
        return wednesday;
    }

    public void setWednesday(OpportunityStatisticsDay wednesday) {
        this.wednesday = wednesday;
    }

    public OpportunityStatisticsDay getThursday() {
        return thursday;
    }

    public void setThursday(OpportunityStatisticsDay thursday) {
        this.thursday = thursday;
    }

    public OpportunityStatisticsDay getFriday() {
        return friday;
    }

    public void setFriday(OpportunityStatisticsDay friday) {
        this.friday = friday;
    }

    public OpportunityStatisticsDay getSaturday() {
        return saturday;
    }

    public void setSaturday(OpportunityStatisticsDay saturday) {
        this.saturday = saturday;
    }

    public OpportunityStatisticsDay getSunday() {
        return sunday;
    }

    public void setSunday(OpportunityStatisticsDay sunday) {
        this.sunday = sunday;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
