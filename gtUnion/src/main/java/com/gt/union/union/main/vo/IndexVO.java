package com.gt.union.union.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.entity.UnionMainTransfer;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 首页
 *
 * @author linweicong
 * @version 2017-11-24 10:36:15
 */
@ApiModel(value = "首页VO")
public class IndexVO {
    @ApiModelProperty(value = "我创建的联盟")
    private UnionMain myCreateUnion;

    @ApiModelProperty(value = "我加入的联盟列表")
    private List<UnionMain> myJoinUnionList;

    @ApiModelProperty(value = "当前联盟")
    private UnionMain currentUnion;

    @ApiModelProperty(value = "当前盟员")
    private UnionMember currentMember;

    @ApiModelProperty(value = "当前联盟盟主")
    private UnionMember ownerMember;

    @ApiModelProperty(value = "盟主转移")
    private UnionMainTransfer unionTransfer;

    @ApiModelProperty(value = "联盟积分")
    private Double integral;

    @ApiModelProperty(value = "成员数")
    private Integer memberCount;

    @ApiModelProperty(value = "剩余盟员数")
    private Integer memberSurplus;

    public UnionMain getMyCreateUnion() {
        return myCreateUnion;
    }

    public void setMyCreateUnion(UnionMain myCreateUnion) {
        this.myCreateUnion = myCreateUnion;
    }

    public List<UnionMain> getMyJoinUnionList() {
        return myJoinUnionList;
    }

    public void setMyJoinUnionList(List<UnionMain> myJoinUnionList) {
        this.myJoinUnionList = myJoinUnionList;
    }

    public UnionMain getCurrentUnion() {
        return currentUnion;
    }

    public void setCurrentUnion(UnionMain currentUnion) {
        this.currentUnion = currentUnion;
    }

    public UnionMember getCurrentMember() {
        return currentMember;
    }

    public void setCurrentMember(UnionMember currentMember) {
        this.currentMember = currentMember;
    }

    public UnionMember getOwnerMember() {
        return ownerMember;
    }

    public void setOwnerMember(UnionMember ownerMember) {
        this.ownerMember = ownerMember;
    }

    public UnionMainTransfer getUnionTransfer() {
        return unionTransfer;
    }

    public void setUnionTransfer(UnionMainTransfer unionTransfer) {
        this.unionTransfer = unionTransfer;
    }

    public Double getIntegral() {
        return integral;
    }

    public void setIntegral(Double integral) {
        this.integral = integral;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getMemberSurplus() {
        return memberSurplus;
    }

    public void setMemberSurplus(Integer memberSurplus) {
        this.memberSurplus = memberSurplus;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }

}
