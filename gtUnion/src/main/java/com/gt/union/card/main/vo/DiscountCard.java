package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author linweicong
 * @version 2018-01-25 08:59:04
 */
@ApiModel(value = "联盟折扣卡")
public class DiscountCard {
    @ApiModelProperty(value = "折扣卡对象")
    private UnionCard card;

    @ApiModelProperty(value = "折扣卡所在联盟")
    private UnionMain union;

    @ApiModelProperty(value = "折扣对象列表")
    private List<UnionMember> memberList;

    public UnionCard getCard() {
        return card;
    }

    public void setCard(UnionCard card) {
        this.card = card;
    }

    public UnionMain getUnion() {
        return union;
    }

    public void setUnion(UnionMain union) {
        this.union = union;
    }

    public List<UnionMember> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<UnionMember> memberList) {
        this.memberList = memberList;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}