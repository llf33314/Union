package com.gt.union.union.main.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 购买联盟盟主服务-支付
 *
 * @author linweicong
 * @version 2017-11-30 15:58:33
 */
@ApiModel(value = "购买联盟盟主服务-支付VO")
public class UnionPermitPayVO {
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
