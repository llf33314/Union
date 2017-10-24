package com.gt.union.opportunity.vo;

import java.util.Date;

/**
 * 商机推荐佣金 VO
 *
 * @author linweicong
 * @version 2017-10-23 11:17:59
 */
public class UnionOpportunityBrokerageVO {

    private Integer id;

    private String clientName;

    private String clientPhone;

    private String businessMsg;

    private String memberName;

    private String unionName;

    private Double acceptPrice;

    private Double brokeragePrice;

    private Integer type;

    private Integer status;

    private Integer incomeId;

    private Date confirmTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getBusinessMsg() {
        return businessMsg;
    }

    public void setBusinessMsg(String businessMsg) {
        this.businessMsg = businessMsg;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getUnionName() {
        return unionName;
    }

    public void setUnionName(String unionName) {
        this.unionName = unionName;
    }

    public Double getAcceptPrice() {
        return acceptPrice;
    }

    public void setAcceptPrice(Double acceptPrice) {
        this.acceptPrice = acceptPrice;
    }

    public Double getBrokeragePrice() {
        return brokeragePrice;
    }

    public void setBrokeragePrice(Double brokeragePrice) {
        this.brokeragePrice = brokeragePrice;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public Integer getIncomeId() {
        return incomeId;
    }

    public void setIncomeId(Integer incomeId) {
        this.incomeId = incomeId;
    }
}
