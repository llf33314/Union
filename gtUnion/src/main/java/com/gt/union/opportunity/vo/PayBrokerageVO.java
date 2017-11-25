package com.gt.union.opportunity.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 佣金支付
 *
 * @author linweicong
 * @version 2017-11-25 09:27:41
 */
@ApiModel(value = "佣金支付")
public class PayBrokerageVO {
    @ApiModelProperty(value = "盟员id")
    private Integer memberId;

    @ApiModelProperty(value = "盟员名称")
    private String memberName;

    @ApiModelProperty(value = "联盟id")
    private Integer unionId;

    @ApiModelProperty(value = "联盟名称")
    private String unionName;

    @ApiModelProperty(value = "往来金额")
    private Double contactMoney;

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

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }

    public String getUnionName() {
        return unionName;
    }

    public void setUnionName(String unionName) {
        this.unionName = unionName;
    }

    public Double getContactMoney() {
        return contactMoney;
    }

    public void setContactMoney(Double contactMoney) {
        this.contactMoney = contactMoney;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
