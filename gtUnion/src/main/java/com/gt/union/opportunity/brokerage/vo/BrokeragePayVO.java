package com.gt.union.opportunity.brokerage.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 佣金支付
 *
 * @author linweicong
 * @version 2017-11-27 16:06:10
 */
@ApiModel(value = "佣金支付VO")
public class BrokeragePayVO {
    @ApiModelProperty(value = "盟员")
    private UnionMember member;

    @ApiModelProperty(value = "联盟")
    private UnionMain union;

    @ApiModelProperty(value = "已支付的商机推荐")
    private List<UnionOpportunity> opportunityList;

    @ApiModelProperty(value = "往来金额")
    private Double contactMoney;

    public UnionMember getMember() {
        return member;
    }

    public void setMember(UnionMember member) {
        this.member = member;
    }

    public UnionMain getUnion() {
        return union;
    }

    public void setUnion(UnionMain union) {
        this.union = union;
    }

    public List<UnionOpportunity> getOpportunityList() {
        return opportunityList;
    }

    public void setOpportunityList(List<UnionOpportunity> opportunityList) {
        this.opportunityList = opportunityList;
    }

    public Double getContactMoney() {
        return contactMoney;
    }

    public void setContactMoney(Double contactMoney) {
        this.contactMoney = contactMoney;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
