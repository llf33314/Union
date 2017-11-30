package com.gt.union.union.main.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.union.main.entity.UnionMainTransfer;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 联盟转移
 *
 * @author linweicong
 * @version 2017-11-24 15:17:37
 */
@ApiModel(value = "联盟转移VO")
public class UnionTransferVO {
    @ApiModelProperty(value = "联盟转移")
    private UnionMainTransfer unionTransfer;

    @ApiModelProperty(value = "盟员")
    private UnionMember member;

    public UnionMainTransfer getUnionTransfer() {
        return unionTransfer;
    }

    public void setUnionTransfer(UnionMainTransfer unionTransfer) {
        this.unionTransfer = unionTransfer;
    }

    public UnionMember getMember() {
        return member;
    }

    public void setMember(UnionMember member) {
        this.member = member;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}