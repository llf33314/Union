package com.gt.union.h5.brokerage.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 商机佣金
 *
 * @author linweicong
 * @version 2017-12-01 14:30:30
 */
@ApiModel(value = "商机佣金")
public class OpportunityBrokerageVO {
    @ApiModelProperty(value = "联盟")
    private UnionMain union;

    @ApiModelProperty(value = "商机推荐者盟员身份")
    private UnionMember fromMember;

    @ApiModelProperty(value = "商机接受者盟员身份")
    private UnionMember toMember;

    @ApiModelProperty(value = "商机")
    private UnionOpportunity opportunity;

    public UnionMain getUnion() {
        return union;
    }

    public void setUnion(UnionMain union) {
        this.union = union;
    }

    public UnionMember getFromMember() {
        return fromMember;
    }

    public void setFromMember(UnionMember fromMember) {
        this.fromMember = fromMember;
    }

    public UnionMember getToMember() {
        return toMember;
    }

    public void setToMember(UnionMember toMember) {
        this.toMember = toMember;
    }

    public UnionOpportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(UnionOpportunity opportunity) {
        this.opportunity = opportunity;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }
}
