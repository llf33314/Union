package com.gt.union.card.sharing.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.sharing.entity.UnionCardSharingRecord;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 售卡分成记录
 *
 * @author linweicong
 * @version 2017-11-24 16:11:46
 */
@ApiModel(value = "售卡分成记录VO")
public class CardSharingRecordVO {
    @ApiModelProperty(value = "联盟卡粉丝信息")
    private UnionCardFan fan;

    @ApiModelProperty(value = "售卡商家")
    private UnionMember member;

    @ApiModelProperty(value = "售卡分成信息")
    private UnionCardSharingRecord sharingRecord;

    public UnionCardFan getFan() {
        return fan;
    }

    public void setFan(UnionCardFan fan) {
        this.fan = fan;
    }

    public UnionMember getMember() {
        return member;
    }

    public void setMember(UnionMember member) {
        this.member = member;
    }

    public UnionCardSharingRecord getSharingRecord() {
        return sharingRecord;
    }

    public void setSharingRecord(UnionCardSharingRecord sharingRecord) {
        this.sharingRecord = sharingRecord;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }
}
