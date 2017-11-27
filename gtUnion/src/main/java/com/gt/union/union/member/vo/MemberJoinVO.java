package com.gt.union.union.member.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.entity.UnionMemberJoin;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 入盟申请
 *
 * @author linweicong
 * @version 2017-11-27 11:46:24
 */
@ApiModel(value = "入盟申请VO")
public class MemberJoinVO {
    @ApiModelProperty(value = "入盟申请信息")
    private UnionMemberJoin memberJoin;

    @ApiModelProperty(value = "入盟者信息")
    private UnionMember joinMember;

    @ApiModelProperty(value = "推荐者信息")
    private UnionMember recommendMember;

    public UnionMemberJoin getMemberJoin() {
        return memberJoin;
    }

    public void setMemberJoin(UnionMemberJoin memberJoin) {
        this.memberJoin = memberJoin;
    }

    public UnionMember getJoinMember() {
        return joinMember;
    }

    public void setJoinMember(UnionMember joinMember) {
        this.joinMember = joinMember;
    }

    public UnionMember getRecommendMember() {
        return recommendMember;
    }

    public void setRecommendMember(UnionMember recommendMember) {
        this.recommendMember = recommendMember;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
