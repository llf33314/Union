package com.gt.union.card.project.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 活动项目
 *
 * @author linweicong
 * @version 2017-11-27 14:42:43
 */
@ApiModel(value = "活动项目VO")
public class CardProjectVO {
    @ApiModelProperty(value = "项目所属盟员")
    private UnionMember member;

    @ApiModelProperty(value = "活动项目")
    private UnionCardProject project;

    @ApiModelProperty(value = "项目优惠列表")
    private List<UnionCardProjectItem> itemList;

    public UnionMember getMember() {
        return member;
    }

    public void setMember(UnionMember member) {
        this.member = member;
    }

    public UnionCardProject getProject() {
        return project;
    }

    public void setProject(UnionCardProject project) {
        this.project = project;
    }

    public List<UnionCardProjectItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<UnionCardProjectItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
