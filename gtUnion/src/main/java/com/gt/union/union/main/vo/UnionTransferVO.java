package com.gt.union.union.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.union.main.entity.UnionMainTransfer;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 联盟盟主权限转移
 *
 * @author linweicong
 * @version 2017-11-24 15:17:37
 */
@ApiModel(value = "联盟盟主权限转移VO")
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
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
