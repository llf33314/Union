package com.gt.union.main.vo;

import com.gt.union.common.annotation.valid.StringLengthValid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2017/9/11 0011.
 */
@ApiModel(value = "UnionMainChargeVO", description = "联盟升级收费实体")
@Data
public class UnionMainChargeVO {
    /**
     * 黑卡是否启用（0：否 1：是）
     */
    @ApiModelProperty(value = "黑卡是否启用（0：否 1：是），统一传启用，即1", required = true)
    @NotNull(message = "请选择黑卡是否启用")
    private Integer blackIsAvailable;

    /**
     * 黑卡旧联盟会员是否要收费（0：否 1：是）
     */
    @NotNull(message = "请选择黑卡老会员升级是否收费")
    @ApiModelProperty(value = "黑卡旧联盟会员是否要收费（0：否 1：是）", required = true)
    private Integer blackIsOldCharge;

    /**
     * 黑卡是否收费（0：否 1：是）
     */
    @ApiModelProperty(value = "黑卡是否收费（0：否 1：是）", required = true)
    @NotNull(message = "请选择黑卡是否收费")
    private Integer blackIsCharge;

    /**
     * 黑卡收费价格
     */
    @ApiModelProperty(value = "黑卡收费价格，设置黑卡收费时必填")
    private Double blackChargePrice;

    /**
     * 黑卡有效期限
     */
    @ApiModelProperty(value = "黑卡有效期限，设置黑卡收费时必填")
    private Integer blackValidityDay;

    /**
     * 黑卡说明
     */
    @ApiModelProperty(value = "黑卡说明，不可超过50字")
    @StringLengthValid(length = 50, message = "黑卡说明内容不可超过50字")
    private String blackIllustration;

    /**
     * 红卡是否启用（0：否 1：是）
     */
    @ApiModelProperty(value = "红卡是否启用（0：否 1：是）", required = true)
    @NotNull(message = "请选择红卡是否启用")
    private Integer redIsAvailable;

    /**
     * 红卡旧联盟会员是否要收费（0：否 1：是）
     */
    @NotNull(message = "请选择红卡老会员升级是否收费")
    @ApiModelProperty(value = "红卡旧联盟会员是否要收费（0：否 1：是）", required = true)
    private Integer redIsOldCharge;

    /**
     * 红卡是否收费（0：否 1：是）
     */
    @ApiModelProperty(value = "红卡是否收费（0：否 1：是）", required = true)
    @NotNull(message = "请选择红卡是否收费")
    private Integer redIsCharge;

    /**
     * 红卡收费价格
     */
    @ApiModelProperty(value = "红卡收费价格，设置红卡收费时必填")
    private Double redChargePrice;

    /**
     * 红卡有效期限
     */
    @ApiModelProperty(value = "红卡有效期限，设置红卡收费时必填")
    private Integer redValidityDay;

    /**
     * 红卡说明
     */
    @ApiModelProperty(value = "红卡说明，不可超过50字")
    @StringLengthValid(length = 50, message = "红卡说明内容不可超过50字")
    private String redIllustration;

    public Integer getBlackIsAvailable() {
        return blackIsAvailable;
    }

    public void setBlackIsAvailable(Integer blackIsAvailable) {
        this.blackIsAvailable = blackIsAvailable;
    }

    public Integer getBlackIsOldCharge() {
        return blackIsOldCharge;
    }

    public void setBlackIsOldCharge(Integer blackIsOldCharge) {
        this.blackIsOldCharge = blackIsOldCharge;
    }

    public Integer getBlackIsCharge() {
        return blackIsCharge;
    }

    public void setBlackIsCharge(Integer blackIsCharge) {
        this.blackIsCharge = blackIsCharge;
    }

    public Double getBlackChargePrice() {
        return blackChargePrice;
    }

    public void setBlackChargePrice(Double blackChargePrice) {
        this.blackChargePrice = blackChargePrice;
    }

    public Integer getBlackValidityDay() {
        return blackValidityDay;
    }

    public void setBlackValidityDay(Integer blackValidityDay) {
        this.blackValidityDay = blackValidityDay;
    }

    public String getBlackIllustration() {
        return blackIllustration;
    }

    public void setBlackIllustration(String blackIllustration) {
        this.blackIllustration = blackIllustration;
    }

    public Integer getRedIsAvailable() {
        return redIsAvailable;
    }

    public void setRedIsAvailable(Integer redIsAvailable) {
        this.redIsAvailable = redIsAvailable;
    }

    public Integer getRedIsOldCharge() {
        return redIsOldCharge;
    }

    public void setRedIsOldCharge(Integer redIsOldCharge) {
        this.redIsOldCharge = redIsOldCharge;
    }

    public Integer getRedIsCharge() {
        return redIsCharge;
    }

    public void setRedIsCharge(Integer redIsCharge) {
        this.redIsCharge = redIsCharge;
    }

    public Double getRedChargePrice() {
        return redChargePrice;
    }

    public void setRedChargePrice(Double redChargePrice) {
        this.redChargePrice = redChargePrice;
    }

    public Integer getRedValidityDay() {
        return redValidityDay;
    }

    public void setRedValidityDay(Integer redValidityDay) {
        this.redValidityDay = redValidityDay;
    }

    public String getRedIllustration() {
        return redIllustration;
    }

    public void setRedIllustration(String redIllustration) {
        this.redIllustration = redIllustration;
    }
}
