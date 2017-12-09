package com.gt.union.opportunity.brokerage.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 佣金结算-我需支付的佣金-支付
 *
 * @author linweicong
 * @version 2017-12-08 14:57:25
 */
@ApiModel(value = "佣金结算-我需支付的佣金-支付VO")
public class OpportunityBrokeragePayVO {
    @ApiModelProperty(value = "支付链接")
    private String payUrl;

    @ApiModelProperty(value = "socket关键字")
    private String socketKey;

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getSocketKey() {
        return socketKey;
    }

    public void setSocketKey(String socketKey) {
        this.socketKey = socketKey;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
