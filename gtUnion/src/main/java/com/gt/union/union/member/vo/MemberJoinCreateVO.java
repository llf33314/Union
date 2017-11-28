package com.gt.union.union.member.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 入盟申请
 *
 * @author linweicong
 * @version 2017-11-24 15:36:11
 */
@ApiModel(value = "入盟申请VO")
public class MemberJoinCreateVO {
    @ApiModelProperty(value = "商家帐号")
    private String busUserName;

    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    @ApiModelProperty(value = "负责人名称")
    private String directorName;

    @ApiModelProperty(value = "负责人电话")
    private String directorPhone;

    @ApiModelProperty(value = "负责人邮箱")
    private String directorEmail;

    @ApiModelProperty(value = "理由")
    private String reason;

    public String getBusUserName() {
        return busUserName;
    }

    public void setBusUserName(String busUserName) {
        this.busUserName = busUserName;
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

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
