package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 办理联盟卡手机和验证码
 *
 * @author linweicong
 * @version 2017-12-13 14:22:17
 */
@ApiModel(value = "办理联盟卡手机和验证码VO")
public class CardPhoneVO {
    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "验证码")
    private String code;

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

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
