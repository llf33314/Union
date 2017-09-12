package com.gt.union.member.vo;

import com.gt.union.common.annotation.valid.StringLengthValid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by Administrator on 2017/9/12 0012.
 */
@ApiModel(value = "UnionMemberVO", description = "盟员实体")
@Data
public class UnionMemberVO {
    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称", required = true)
    @NotBlank(message = "企业名称内容不能为空")
    @StringLengthValid(length = 10, message = "企业名称不可超过10个字")
    private String enterpriseName;

    /**
     * 企业地址
     */
    @ApiModelProperty(value = "企业地址", required = true)
    @NotBlank(message = "企业地址不能为空")
    private String enterpriseAddress;

    /**
     * 负责人名称
     */
    @ApiModelProperty(value = "负责人名称")
    @StringLengthValid(length = 10, message = "负责人名称不可超过10个字")
    private String directorName;

    /**
     * 负责人联系电话
     */
    @ApiModelProperty(value = "负责人联系电话", required = true)
    @NotBlank(message = "负责人联系电话内容不能为空")
    @Pattern(regexp = "^1[3|4|5|6|7|8][0-9][0-9]{8}$", message = "负责人联系电话有误")
    private String directorPhone;

    /**
     * 负责人邮箱
     */
    @ApiModelProperty(value = "负责人邮箱")
    @Email(message = "负责人邮箱有误", regexp = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")
    private String directorEmail;

    /**
     * 地址经度
     */
    @ApiModelProperty(value = "地址经度")
    @NotNull(message = "地址经度不能为空")
    private String addressLongitude;

    /**
     * 地址维度
     */
    @ApiModelProperty(value = "地址维度")
    @NotNull(message = "地址维度不能为空")
    private String addressLatitude;

    /**
     * 短信通知手机号
     */
    @ApiModelProperty(value = "短信通知手机号", required = true)
    @NotBlank(message = "短信通知手机号不能为空")
    @Pattern(regexp = "^1[3|4|5|6|7|8][0-9][0-9]{8}$", message = "短信通知手机号有误")
    private String notifyPhone;

    /**
     * 地址省份code
     */
    @NotBlank(message = "请选择省")
    @ApiModelProperty(value = "地址省份code")
    private String addressProvinceCode;

    /**
     * 地址城市code
     */
    @NotBlank(message = "请选择市")
    @ApiModelProperty(value = "地址城市code")
    private String addressCityCode;

    /**
     * 地址区code
     */
    @NotBlank(message = "请选择区")
    @ApiModelProperty(value = "地址区code")
    private String addressDistrictCode;

    /**
     * 积分兑换率
     */
    @ApiModelProperty(value = "积分兑换率 (0-30%]，联盟设置了开启积分时，必填")
    private Double integralExchangePercent;

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getEnterpriseAddress() {
        return enterpriseAddress;
    }

    public void setEnterpriseAddress(String enterpriseAddress) {
        this.enterpriseAddress = enterpriseAddress;
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

    public String getNotifyPhone() {
        return notifyPhone;
    }

    public void setNotifyPhone(String notifyPhone) {
        this.notifyPhone = notifyPhone;
    }

    public String getAddressProvinceCode() {
        return addressProvinceCode;
    }

    public void setAddressProvinceCode(String addressProvinceCode) {
        this.addressProvinceCode = addressProvinceCode;
    }

    public String getAddressCityCode() {
        return addressCityCode;
    }

    public void setAddressCityCode(String addressCityCode) {
        this.addressCityCode = addressCityCode;
    }

    public String getAddressDistrictCode() {
        return addressDistrictCode;
    }

    public void setAddressDistrictCode(String addressDistrictCode) {
        this.addressDistrictCode = addressDistrictCode;
    }

    public Double getIntegralExchangePercent() {
        return integralExchangePercent;
    }

    public void setIntegralExchangePercent(Double integralExchangePercent) {
        this.integralExchangePercent = integralExchangePercent;
    }
}
