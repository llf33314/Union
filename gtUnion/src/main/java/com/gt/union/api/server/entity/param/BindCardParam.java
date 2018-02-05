package com.gt.union.api.server.entity.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hongjiye
 * @time 2017/9/4 0004.
 */
@ApiModel(value = "BindCardParam", description = "绑定联盟卡参数实体")
@Data
public class BindCardParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "电话号码", required = true)
    private String phone;

    @ApiModelProperty(value = "验证码", required = true)
    private String code;

    @ApiModelProperty(value = "粉丝用户id", required = true)
    private Integer memberId;

    @ApiModelProperty(value = "商家id", required = true)
    private Integer busId;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }
}
