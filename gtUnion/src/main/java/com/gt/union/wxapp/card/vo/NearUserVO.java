package com.gt.union.wxapp.card.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotations.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author hongjiye
 * @time 2018-01-18 9:21
 **/
@ApiModel(value = "附近商家列表实体")
public class NearUserVO {

    @ApiModelProperty(value = "盟员id")
    private Integer unionMemberId;

    @ApiModelProperty(value = "盟员企业名称")
    private String enterpriseName;

    @ApiModelProperty(value = "盟员企业地址")
    private String address;

    @ApiModelProperty(value = "地址经度")
    private String addressLongitude;

    @ApiModelProperty(value = "地址维度")
    private String addressLatitude;

    public Integer getUnionMemberId() {
        return unionMemberId;
    }

    public void setUnionMemberId(Integer unionMemberId) {
        this.unionMemberId = unionMemberId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressLongitude() {
        return addressLongitude;
    }

    public void setAddressLongitude(String addressLongitude) {
        this.addressLongitude = addressLongitude;
    }

    public String getAddressLatitude() {
        return addressLatitude;
    }

    public void setAddressLatitude(String addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
