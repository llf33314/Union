package com.gt.union.opportunity.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 佣金比例
 *
 * @author linweicong
 * @version 2017-11-25 10:09:28
 */
@ApiModel(value = "佣金比例VO")
public class OpportunityRatioVO {
    @ApiModelProperty(value = "盟员")
    private UnionMember member;

    @ApiModelProperty(value = "我给的佣金比例")
    private Double ratioFromMe;

    @ApiModelProperty(value = "给我的佣金比例")
    private Double ratioToMe;

    public UnionMember getMember() {
        return member;
    }

    public void setMember(UnionMember member) {
        this.member = member;
    }

    public Double getRatioFromMe() {
        return ratioFromMe;
    }

    public void setRatioFromMe(Double ratioFromMe) {
        this.ratioFromMe = ratioFromMe;
    }

    public Double getRatioToMe() {
        return ratioToMe;
    }

    public void setRatioToMe(Double ratioToMe) {
        this.ratioToMe = ratioToMe;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }
}
