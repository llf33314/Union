package com.gt.union.card.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.card.entity.UnionConsumeProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 消费核销
 *
 * @author linweicong
 * @version 2017-11-25 11:21:28
 */
@ApiModel(value = "消费核销")
public class ConsumeVO {
    @ApiModelProperty(value = "联盟名称")
    private String unionName;

    @ApiModelProperty(value = "门店名称")
    private String shopName;

    @ApiModelProperty(value = "联盟卡号")
    private String cardNumber;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "消费金额")
    private Double consumeMoney;

    @ApiModelProperty(value = "支付金额")
    private Double payMoney;

    @ApiModelProperty(value = "支付状态(1:未支付 2:已支付 3:已退款)")
    private Integer payStatus;

    @ApiModelProperty(value = "消费时间")
    private Date consumeTime;

    @ApiModelProperty(value = "活动项目列表")
    private List<UnionConsumeProject> consumeProjectList;

    public String getUnionName() {
        return unionName;
    }

    public void setUnionName(String unionName) {
        this.unionName = unionName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getConsumeMoney() {
        return consumeMoney;
    }

    public void setConsumeMoney(Double consumeMoney) {
        this.consumeMoney = consumeMoney;
    }

    public Double getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Double payMoney) {
        this.payMoney = payMoney;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Date getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(Date consumeTime) {
        this.consumeTime = consumeTime;
    }

    public List<UnionConsumeProject> getConsumeProjectList() {
        return consumeProjectList;
    }

    public void setConsumeProjectList(List<UnionConsumeProject> consumeProjectList) {
        this.consumeProjectList = consumeProjectList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
