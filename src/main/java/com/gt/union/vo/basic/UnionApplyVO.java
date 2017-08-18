package com.gt.union.vo.basic;

import com.gt.union.common.annotation.valid.StringLengthValid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by Administrator on 2017/7/24 0024.
 */
@ApiModel( value = "UnionApplyVO", description = "申请推荐加盟实体" )
@Data
public class UnionApplyVO {
    /**
     * 盟员商家帐号
     */
    @ApiModelProperty( value = "推荐时的盟员账号" )
    private String userName;

    /**
     * 申请推荐理由
     */
    @ApiModelProperty( value = "申请推荐理由")
    @StringLengthValid(length = 20, message = "申请推荐理由不可超过20字")
    private String applyReason;

    /**
     * 邮箱
     */
    @ApiModelProperty( value = "邮箱")
    @Email(message = "邮箱有误", regexp = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")
    private String directorEmail;

    /**
     * 负责人姓名
     */
    @ApiModelProperty( value = "负责人姓名")
    @StringLengthValid(length = 10, message = "负责人内容不可超过10字")
    private String directorName;

    /**
     * 联系电话
     */
    @ApiModelProperty( value = "联系电话" ,required = true)
    @NotBlank( message = "联系电话内容不能为空")
    @Pattern(regexp = "^1[3|4|5|6|7|8][0-9][0-9]{8}$", message = "联系电话有误")
    private String directorPhone;

    /**
     * 企业名称
     */
    @ApiModelProperty( value = "企业名称" ,required = true)
    @NotBlank( message = "企业名称内容不能为空")
    @StringLengthValid(length = 10, message = "企业名称内容不可超过10字")
    private String enterpriseName;

    /**
     * 联盟id
     */
    @ApiModelProperty( value = "联盟id" ,required = true)
    @NotNull(message = "请选择联盟")
    private Integer unionId;

    /**
     * 申请推荐类型
     */
    private Integer applyType;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getApplyReason() {
        return applyReason;
    }

    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }

    public String getDirectorEmail() {
        return directorEmail;
    }

    public void setDirectorEmail(String directorEmail) {
        this.directorEmail = directorEmail;
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

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }

    public Integer getApplyType() {
        return applyType;
    }

    public void setApplyType(Integer applyType) {
        this.applyType = applyType;
    }
}

