package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 联盟卡卡号搜索
 *
 * @author linweicong
 * @version 2017-11-27 16:44:02
 */
@ApiModel(value = "联盟卡卡号搜索VO")
public class CardFanSearchVO {
    @ApiModelProperty(value = "联盟卡粉丝信息")
    private UnionCardFan fan;

    @ApiModelProperty(value = "联盟积分")
    private Double integral;

    @ApiModelProperty(value = "折扣卡")
    private UnionCard discountCard;

    @ApiModelProperty(value = "盟员")
    private UnionMember member;

    @ApiModelProperty(value = "联盟")
    private UnionMain union;

    @ApiModelProperty(value = "联盟列表")
    private List<UnionMain> unionList;

    public UnionCardFan getFan() {
        return fan;
    }

    public void setFan(UnionCardFan fan) {
        this.fan = fan;
    }

    public Double getIntegral() {
        return integral;
    }

    public void setIntegral(Double integral) {
        this.integral = integral;
    }

    public UnionCard getDiscountCard() {
        return discountCard;
    }

    public void setDiscountCard(UnionCard discountCard) {
        this.discountCard = discountCard;
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

    public List<UnionMain> getUnionList() {
        return unionList;
    }

    public void setUnionList(List<UnionMain> unionList) {
        this.unionList = unionList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
