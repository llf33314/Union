package com.gt.union.opportunity.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 佣金支付详情
 *
 * @author linweicong
 * @version 2017-11-25 09:35:22
 */
@ApiModel(value = "佣金支付详情")
public class PayBrokerageDetailVO {
    @ApiModelProperty(value = "盟员名称")
    private String memberName;

    @ApiModelProperty(value = "联盟名称")
    private String unionName;

    @ApiModelProperty(value = "往来金额")
    private Double contactMoney;

    @ApiModelProperty(value = "佣金明细列表")
    private List<PayBrokerageDetail> detailList;

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

    public Double getContactMoney() {
        return contactMoney;
    }

    public void setContactMoney(Double contactMoney) {
        this.contactMoney = contactMoney;
    }

    public List<PayBrokerageDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<PayBrokerageDetail> detailList) {
        this.detailList = detailList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
