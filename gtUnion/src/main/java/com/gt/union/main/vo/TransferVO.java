package com.gt.union.main.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 联盟转移
 *
 * @author linweicong
 * @version 2017-11-24 15:17:37
 */
@ApiModel(value = "联盟转移")
public class TransferVO {
    @ApiModelProperty(value = "转移id")
    private Integer id;

    @ApiModelProperty(value = "盟员")
    private UnionMember member;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
