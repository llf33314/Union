package com.gt.union.member.vo;

import com.gt.union.common.annotation.valid.StringLengthValid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * 加入联盟申请信息 VO
 *
 * @author linweicong
 * @version 2017-10-23 08:34:54
 */
@ApiModel(value = "UnionMemberJoinVO", description = "加入联盟申请信息实体")
@Data
public class UnionMemberJoinVO {
    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称", required = true)
    private String busUserName;

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称", required = true)
    @NotBlank(message = "企业名称内容不能为空")
    @StringLengthValid(length = 10, message = "企业名称内容不可超过10字")
    private String enterpriseName;

    /**
     * 负责人名称
     */
    @ApiModelProperty(value = "负责人名称")
    @StringLengthValid(length = 10, message = "负责人名称内容不可超过10字")
    private String directorName;

    /**
     * 负责人联系电话
     */
    @ApiModelProperty(value = "负责人联系电话", required = true)
    @NotBlank(message = "联系电话内容不能为空")
    @Pattern(regexp = "(^1[3|4|5|6|7|8][0-9][0-9]{8}$)|(^0\\d{2,3}-?\\d{7,8}$)", message = "联系电话有误")
    private String directorPhone;

    /**
     * 负责人邮箱
     */
    @ApiModelProperty(value = "负责人邮箱")
    @Email(message = "邮箱有误", regexp = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")
    private String directorEmail;

    /**
     * 申请理由或推荐理由
     */
    @ApiModelProperty(value = "申请理由或推荐理由")
    @StringLengthValid(length = 20, message = "申请或推荐理由不可超过20字")
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
}
