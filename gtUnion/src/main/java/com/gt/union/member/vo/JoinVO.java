package com.gt.union.member.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 入盟信息
 *
 * @author linweicong
 * @version 2017-11-24 15:54:18
 */
@ApiModel(value = "入盟信息")
public class JoinVO {
    @ApiModelProperty(value = "加盟id")
    private Integer joinId;

    @ApiModelProperty(value = "加盟企业名称")
    private String enterpriseName;

    @ApiModelProperty(value = "加盟企业负责人名称")
    private String directorName;

    @ApiModelProperty(value = "加盟企业负责人电话")
    private String directorPhone;

    @ApiModelProperty(value = "加盟企业负责人邮箱")
    private String directorEmail;

    @ApiModelProperty(value = "加盟申请或推荐理由")
    private String reason;

    @ApiModelProperty(value = "加盟或推荐时间")
    private Date createTime;

    @ApiModelProperty(value = "推荐人的企业名称")
    private String recommendEnterpriseName;

    public Integer getJoinId() {
        return joinId;
    }

    public void setJoinId(Integer joinId) {
        this.joinId = joinId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public String getDirectorPhone() {
        return directorPhone;
    }

    public void setDirectorPhone(String directorPhone) {
        this.directorPhone = directorPhone;
    }

    public String getDirectorEmail() {
        return directorEmail;
    }

    public void setDirectorEmail(String directorEmail) {
        this.directorEmail = directorEmail;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRecommendEnterpriseName() {
        return recommendEnterpriseName;
    }

    public void setRecommendEnterpriseName(String recommendEnterpriseName) {
        this.recommendEnterpriseName = recommendEnterpriseName;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
