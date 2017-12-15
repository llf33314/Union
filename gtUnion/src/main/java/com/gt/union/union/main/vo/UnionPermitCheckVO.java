package com.gt.union.union.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 检查联盟盟主服务
 *
 * @author linweicong
 * @version 2017-11-24 14:45:48
 */
@ApiModel(value = "检查联盟盟主服务VO")
public class UnionPermitCheckVO {
    @ApiModelProperty(value = "是否需要购买(0:否 1:是)")
    private Integer isPay;

    @ApiModelProperty(value = "许可id")
    private Integer permitId;

    public Integer getIsPay() {
        return isPay;
    }

    public void setIsPay(Integer isPay) {
        this.isPay = isPay;
    }

    public Integer getPermitId() {
        return permitId;
    }

    public void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }
}
