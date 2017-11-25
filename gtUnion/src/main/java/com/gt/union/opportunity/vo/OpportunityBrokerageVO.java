package com.gt.union.opportunity.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 佣金
 *
 * @author linweicong
 * @version 2017-11-25 09:10:03
 */
@ApiModel(value = "佣金")
public class OpportunityBrokerageVO {
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

    @ApiModelProperty(value = "受理价格")
    private Double acceptPrice;

    @ApiModelProperty(value = "佣金金额")
    private Double brokerageMoney;

    @ApiModelProperty(value = "推荐类型(1:线上 2:线下)")
    private Integer type;

    @ApiModelProperty(value = "是否已结算(0:否 1:是)")
    private Integer isClose;

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

    public Double getAcceptPrice() {
        return acceptPrice;
    }

    public void setAcceptPrice(Double acceptPrice) {
        this.acceptPrice = acceptPrice;
    }

    public Double getBrokerageMoney() {
        return brokerageMoney;
    }

    public void setBrokerageMoney(Double brokerageMoney) {
        this.brokerageMoney = brokerageMoney;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsClose() {
        return isClose;
    }

    public void setIsClose(Integer isClose) {
        this.isClose = isClose;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
