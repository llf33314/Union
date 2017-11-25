package com.gt.union.main.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author linweicong
 * @version 2017-11-24 10:36:15
 */
@ApiModel(value = "首页VO")
public class IndexVO {
    @ApiModelProperty(value = "我创建的联盟")
    private MyCreateUnion myCreateUnion;

    @ApiModelProperty(value = "我加入的联盟列表")
    private List<MyJoinUnion> myJoinUnionList;

    @ApiModelProperty(value = "当前联盟")
    private CurrentUnion currentUnion;

    @ApiModelProperty(value = "联盟转移id")
    private Integer unionTransferId;

    public MyCreateUnion getMyCreateUnion() {
        return myCreateUnion;
    }

    public void setMyCreateUnion(MyCreateUnion myCreateUnion) {
        this.myCreateUnion = myCreateUnion;
    }

    public List<MyJoinUnion> getMyJoinUnionList() {
        return myJoinUnionList;
    }

    public void setMyJoinUnionList(List<MyJoinUnion> myJoinUnionList) {
        this.myJoinUnionList = myJoinUnionList;
    }

    public CurrentUnion getCurrentUnion() {
        return currentUnion;
    }

    public void setCurrentUnion(CurrentUnion currentUnion) {
        this.currentUnion = currentUnion;
    }

    public Integer getUnionTransferId() {
        return unionTransferId;
    }

    public void setUnionTransferId(Integer unionTransferId) {
        this.unionTransferId = unionTransferId;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
