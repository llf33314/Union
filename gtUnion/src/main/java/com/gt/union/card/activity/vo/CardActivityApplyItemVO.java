package com.gt.union.card.activity.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 办理联盟卡-查询服务项目数详情
 *
 * @author linweicong
 * @version 2017-11-30 15:00:03
 */
@ApiModel(value = "办理联盟卡-查询服务项目数详情VO")
public class CardActivityApplyItemVO {
    @ApiModelProperty(value = "盟员")
    private UnionMember member;

    @ApiModelProperty(value = "项目优惠列表")
    private List<UnionCardProjectItem> itemList;

    public UnionMember getMember() {
        return member;
    }

    public void setMember(UnionMember member) {
        this.member = member;
    }

    public List<UnionCardProjectItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<UnionCardProjectItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }
}
