package com.gt.union.card.sharing.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.card.sharing.entity.UnionCardSharingRatio;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 售卡分成比例
 *
 * @author linweicong
 * @version 2017-11-24 16:27:43
 */
@ApiModel(value = "售卡分成比例VO")
public class CardSharingRatioVO {
    @ApiModelProperty(value = "盟员")
    private UnionMember member;

    @ApiModelProperty(value = "售卡分成比例")
    private UnionCardSharingRatio sharingRatio;

    public UnionMember getMember() {
        return member;
    }

    public void setMember(UnionMember member) {
        this.member = member;
    }

    public UnionCardSharingRatio getSharingRatio() {
        return sharingRatio;
    }

    public void setSharingRatio(UnionCardSharingRatio sharingRatio) {
        this.sharingRatio = sharingRatio;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }
}
