package com.gt.union.union.member.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.entity.UnionMemberOut;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 退盟申请
 *
 * @author linweicong
 * @version 2017-11-24 18:21:32
 */
@ApiModel(value = "退盟申请VO")
public class MemberOutVO {
    @ApiModelProperty(value = "退盟申请")
    private UnionMemberOut memberOut;

    @ApiModelProperty(value = "退盟盟员")
    private UnionMember member;
    
    @ApiModelProperty(value = "剩余过渡期")
    private Integer periodDay;

    public UnionMemberOut getMemberOut() {
        return memberOut;
    }

    public void setMemberOut(UnionMemberOut memberOut) {
        this.memberOut = memberOut;
    }

    public UnionMember getMember() {
        return member;
    }

    public void setMember(UnionMember member) {
        this.member = member;
    }

    public Integer getPeriodDay() {
        return periodDay;
    }

    public void setPeriodDay(Integer periodDay) {
        this.periodDay = periodDay;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }

}
