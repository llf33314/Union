package com.gt.union.h5.brokerage.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.api.bean.session.BusUser;
import com.gt.union.finance.verifier.entity.UnionVerifier;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * h5佣金平台用户
 *
 * @author linweicong
 * @version 2017-12-01 11:39:37
 */
@ApiModel(value = "h5佣金平台用户")
public class H5BrokerageUser {
    @ApiModelProperty(value = "佣金平台管理员")
    private UnionVerifier verifier;

    @ApiModelProperty(value = "商家")
    private BusUser busUser;

    public UnionVerifier getVerifier() {
        return verifier;
    }

    public void setVerifier(UnionVerifier verifier) {
        this.verifier = verifier;
    }

    public BusUser getBusUser() {
        return busUser;
    }

    public void setBusUser(BusUser busUser) {
        this.busUser = busUser;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }

}
