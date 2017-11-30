package com.gt.union.opportunity.main.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 商机
 *
 * @author linweicong
 * @version 2017-11-25 08:44:13
 */
@ApiModel(value = "商机VO")
public class OpportunityVO {
    @ApiModelProperty(value = "商机")
    private UnionOpportunity opportunity;

    @ApiModelProperty(value = "商机来源")
    private UnionMember fromMember;

    @ApiModelProperty(value = "推荐商家")
    private UnionMember toMember;

    @ApiModelProperty(value = "所属联盟")
    private UnionMain union;

    public UnionOpportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(UnionOpportunity opportunity) {
        this.opportunity = opportunity;
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

    public UnionMain getUnion() {
        return union;
    }

    public void setUnion(UnionMain union) {
        this.union = union;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}