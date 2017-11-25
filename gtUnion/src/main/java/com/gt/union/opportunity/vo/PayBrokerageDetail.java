package com.gt.union.opportunity.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 佣金支付详情
 *
 * @author linweicong
 * @version 2017-11-25 09:37:17
 */
@ApiModel(value = "佣金支付详情")
public class PayBrokerageDetail {
    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "客户电话")
    private String clientPhone;

    @ApiModelProperty(value = "佣金金额")
    private Double brokerageMoney;

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
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

    public Double getBrokerageMoney() {
        return brokerageMoney;
    }

    public void setBrokerageMoney(Double brokerageMoney) {
        this.brokerageMoney = brokerageMoney;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
