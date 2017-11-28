package com.gt.union.union.main.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.entity.UnionMainDict;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 创建联盟
 *
 * @author linweicong
 * @version 2017-11-24 14:51:03
 */
@ApiModel(value = "联盟创建VO")
public class UnionCreateVO {
    @ApiModelProperty(value = "许可id")
    private Integer permitId;

    @ApiModelProperty(value = "新建的盟员信息")
    private UnionMember member;

    @ApiModelProperty(value = "新建的联盟信息")
    private UnionMain union;

    @ApiModelProperty(value = "入盟必填字段列表")
    private List<UnionMainDict> itemList;

    public Integer getPermitId() {
        return permitId;
    }

    public void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public UnionMember getMember() {
        return member;
    }

    public void setMember(UnionMember member) {
        this.member = member;
    }

    public UnionMain getUnion() {
        return union;
    }

    public void setUnion(UnionMain union) {
        this.union = union;
    }

    public List<UnionMainDict> getItemList() {
        return itemList;
    }

    public void setItemList(List<UnionMainDict> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
