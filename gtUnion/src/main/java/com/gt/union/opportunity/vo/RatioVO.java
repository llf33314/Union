package com.gt.union.opportunity.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 佣金比例
 *
 * @author linweicong
 * @version 2017-11-25 10:09:28
 */
@ApiModel(value = "佣金比例")
public class RatioVO {
    @ApiModelProperty(value = "盟员id")
    private Integer memberId;

    @ApiModelProperty(value = "盟员名称")
    private String memberName;

    @ApiModelProperty(value = "我给的佣金比例")
    private Double ratioFromMe;

    @ApiModelProperty(value = "给我的佣金比例")
    private Double ratioToMe;

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
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
        return JSONArray.toJSONString(this);
    }
}
