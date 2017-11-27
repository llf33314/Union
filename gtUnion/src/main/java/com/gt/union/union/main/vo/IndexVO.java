package com.gt.union.union.main.vo;

import com.alibaba.fastjson.JSONArray;
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

    @ApiModelProperty(value = "盟主转移")
    private UnionMainTransfer unionTransfer;

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

    public UnionMainTransfer getUnionTransfer() {
        return unionTransfer;
    }

    public void setUnionTransfer(UnionMainTransfer unionTransfer) {
        this.unionTransfer = unionTransfer;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
