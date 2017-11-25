package com.gt.union.opportunity.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 商机
 *
 * @author linweicong
 * @version 2017-11-25 08:44:13
 */
@ApiModel(value = "商机")
public class OpportunityVO {
    @ApiModelProperty(value = "商机id")
    private Integer opportunityId;

    @ApiModelProperty(value = "客户姓名")
    private String clientName;

    @ApiModelProperty(value = "客户电话")
    private String clientPhone;

    @ApiModelProperty(value = "业务备注")
    private String businessMsg;

    @ApiModelProperty(value = "商机来源")
    private String fromMemberName;

    @ApiModelProperty(value = "推荐商家")
    private String toMemberName;

    @ApiModelProperty(value = "所属联盟id")
    private Integer unionId;

    @ApiModelProperty(value = "所属联盟名称")
    private String unionName;

    @ApiModelProperty(value = "受理状态(1:受理中 2:已接受 3:已拒绝)")
    private Integer acceptStatus;

    public Integer getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Integer opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getBusinessMsg() {
        return businessMsg;
    }

    public void setBusinessMsg(String businessMsg) {
        this.businessMsg = businessMsg;
    }

    public String getFromMemberName() {
        return fromMemberName;
    }

    public void setFromMemberName(String fromMemberName) {
        this.fromMemberName = fromMemberName;
    }

    public String getToMemberName() {
        return toMemberName;
    }

    public void setToMemberName(String toMemberName) {
        this.toMemberName = toMemberName;
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

    public Integer getAcceptStatus() {
        return acceptStatus;
    }

    public void setAcceptStatus(Integer acceptStatus) {
        this.acceptStatus = acceptStatus;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
