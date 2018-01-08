package com.gt.union.union.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.union.main.entity.UnionMainPackage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 盟主服务套餐
 *
 * @author linweicong
 * @version 2017-12-05 09:25:06
 */
@ApiModel(value = "盟主服务套餐VO")
public class UnionPackageVO {
    @ApiModelProperty(value = "商家版本名称，如升级版")
    private String busVersionName;

    @ApiModelProperty(value = "联盟版本名称，如盟主版")
    private String unionVersionName;

    @ApiModelProperty(value = "套餐列表")
    private List<UnionMainPackage> packageList;

    public String getBusVersionName() {
        return busVersionName;
    }

    public void setBusVersionName(String busVersionName) {
        this.busVersionName = busVersionName;
    }

    public String getUnionVersionName() {
        return unionVersionName;
    }

    public void setUnionVersionName(String unionVersionName) {
        this.unionVersionName = unionVersionName;
    }

    public List<UnionMainPackage> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<UnionMainPackage> packageList) {
        this.packageList = packageList;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
