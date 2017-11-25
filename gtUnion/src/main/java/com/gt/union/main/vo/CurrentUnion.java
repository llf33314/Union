package com.gt.union.main.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 当前联盟
 *
 * @author linweicong
 * @version 2017-11-24 11:33:10
 */
@ApiModel(value = "当前联盟")
public class CurrentUnion {
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "联盟名称")
    private String unionName;

    @ApiModelProperty(value = "盟员名称")
    private String memberName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "图标")
    private String img;

    @ApiModelProperty(value = "说明")
    private String illustration;

    @ApiModelProperty(value = "是否开启积分")
    private Integer isIntegral;

    @ApiModelProperty(value = "积分")
    private Integer integral;

    @ApiModelProperty(value = "成员数")
    private Integer memberCount;

    @ApiModelProperty(value = "剩余盟员数")
    private Integer memberSurplus;

    @ApiModelProperty(value = "是否盟主")
    private Integer isUnionOwner;

    @ApiModelProperty(value = "盟主名称")
    private String ownerName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnionName() {
        return unionName;
    }

    public void setUnionName(String unionName) {
        this.unionName = unionName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIllustration() {
        return illustration;
    }

    public void setIllustration(String illustration) {
        this.illustration = illustration;
    }

    public Integer getIsIntegral() {
        return isIntegral;
    }

    public void setIsIntegral(Integer isIntegral) {
        this.isIntegral = isIntegral;
    }

    public Integer getIntegral() {
        return integral;
    }

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getMemberSurplus() {
        return memberSurplus;
    }

    public void setMemberSurplus(Integer memberSurplus) {
        this.memberSurplus = memberSurplus;
    }

    public Integer getIsUnionOwner() {
        return isUnionOwner;
    }

    public void setIsUnionOwner(Integer isUnionOwner) {
        this.isUnionOwner = isUnionOwner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
