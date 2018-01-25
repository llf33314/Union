package com.gt.union.opportunity.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 商机佣金统计
 *
 * @author linweicong
 * @version 2017-11-25 09:48:20
 */
@ApiModel(value = "商机佣金统计VO")
public class OpportunityStatisticsVO {
    @ApiModelProperty(value = "未结算佣金收入")
    private Double unPaidIncome;

    @ApiModelProperty(value = "未结算佣金收入详情列表")
    private List<OpportunityStatisticsDetail> unPaidIncomeDetailList;

    @ApiModelProperty(value = "已结算佣金收入")
    private Double paidIncome;

    @ApiModelProperty(value = "已结算佣金收入详情列表")
    private List<OpportunityStatisticsDetail> paidIncomeDetailList;

    @ApiModelProperty(value = "总收入")
    private Double incomeSum;

    @ApiModelProperty(value = "未支付佣金支出")
    private Double unPaidExpense;

    @ApiModelProperty(value = "未支付佣金支出详情列表")
    private List<OpportunityStatisticsDetail> unPaidExpenseDetailList;

    @ApiModelProperty(value = "已支付佣金支出")
    private Double paidExpense;

    @ApiModelProperty(value = "已支付佣金支出详情列表")
    private List<OpportunityStatisticsDetail> paidExpenseDetailList;

    @ApiModelProperty(value = "总支出")
    private Double expenseSum;

    public Double getUnPaidIncome() {
        return unPaidIncome;
    }

    public void setUnPaidIncome(Double unPaidIncome) {
        this.unPaidIncome = unPaidIncome;
    }

    public List<OpportunityStatisticsDetail> getUnPaidIncomeDetailList() {
        return unPaidIncomeDetailList;
    }

    public void setUnPaidIncomeDetailList(List<OpportunityStatisticsDetail> unPaidIncomeDetailList) {
        this.unPaidIncomeDetailList = unPaidIncomeDetailList;
    }

    public Double getPaidIncome() {
        return paidIncome;
    }

    public void setPaidIncome(Double paidIncome) {
        this.paidIncome = paidIncome;
    }

    public List<OpportunityStatisticsDetail> getPaidIncomeDetailList() {
        return paidIncomeDetailList;
    }

    public void setPaidIncomeDetailList(List<OpportunityStatisticsDetail> paidIncomeDetailList) {
        this.paidIncomeDetailList = paidIncomeDetailList;
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

    public List<OpportunityStatisticsDetail> getUnPaidExpenseDetailList() {
        return unPaidExpenseDetailList;
    }

    public void setUnPaidExpenseDetailList(List<OpportunityStatisticsDetail> unPaidExpenseDetailList) {
        this.unPaidExpenseDetailList = unPaidExpenseDetailList;
    }

    public Double getPaidExpense() {
        return paidExpense;
    }

    public void setPaidExpense(Double paidExpense) {
        this.paidExpense = paidExpense;
    }

    public List<OpportunityStatisticsDetail> getPaidExpenseDetailList() {
        return paidExpenseDetailList;
    }

    public void setPaidExpenseDetailList(List<OpportunityStatisticsDetail> paidExpenseDetailList) {
        this.paidExpenseDetailList = paidExpenseDetailList;
    }

    public Double getExpenseSum() {
        return expenseSum;
    }

    public void setExpenseSum(Double expenseSum) {
        this.expenseSum = expenseSum;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
