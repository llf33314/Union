package com.gt.union.card.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 售卡分成比例
 *
 * @author linweicong
 * @version 2017-11-24 16:27:43
 */
@ApiModel(value = "售卡分成比例")
public class SharingRatioVO {
    @ApiModelProperty(value = "盟员id")
    private Integer memberId;

    @ApiModelProperty(value = "盟员名称")
    private String memberName;

    @ApiModelProperty(value = "售卡分成比例")
    private Double sharingRatio;

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Double getSharingRatio() {
        return sharingRatio;
    }

    public void setSharingRatio(Double sharingRatio) {
        this.sharingRatio = sharingRatio;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
