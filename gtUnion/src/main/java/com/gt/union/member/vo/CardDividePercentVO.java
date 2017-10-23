package com.gt.union.member.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 售卡分成比例 VO
 *
 * @author linweicong
 * @version 2017-10-23 08:34:54
 */
@ApiModel(value = "UnionMemberCardDividePercentVO", description = "更新售卡分成比例实体")
@Data
public class CardDividePercentVO {
    /**
     * 盟员身份id
     */
    @ApiModelProperty(value = "盟员身份id", required = true)
    @NotNull(message = "盟员身份id不能为空")
    private Integer memberId;

    @ApiModelProperty(value = "售卡分成比例", required = true)
    @NotNull(message = "售卡分成比例不能为空")
    private Double cardDividePercent;

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Double getCardDividePercent() {
        return cardDividePercent;
    }

    public void setCardDividePercent(Double cardDividePercent) {
        this.cardDividePercent = cardDividePercent;
    }
}
