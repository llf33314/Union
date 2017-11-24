package com.gt.union.card.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 售卡分成记录
 *
 * @author linweicong
 * @version 2017-11-24 16:11:46
 */
@ApiModel(value = "售卡分成记录")
public class SharingRecordVO {
    @ApiModelProperty(value = "售卡时间")
    private Date createTime;

    @ApiModelProperty(value = "联盟卡号")
    private String cardNumber;

    @ApiModelProperty(value = "售卡金额")
    private Double sellPrice;

    @ApiModelProperty(value = "售卡分成")
    private Double sharingPrice;

    @ApiModelProperty(value = "售卡来源")
    private String memberName;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Double getSharingPrice() {
        return sharingPrice;
    }

    public void setSharingPrice(Double sharingPrice) {
        this.sharingPrice = sharingPrice;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
