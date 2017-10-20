package com.gt.union.common.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.common.constant.CommonConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel( value = "GTJsonResult", description = "响应结果实体" )
@Data
public class GTJsonResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty( value = "true:成功;false:失败")
    private boolean success; //true:成功;false:失败-由方法标签控制

    @ApiModelProperty( value = "成功返回的具体数据")
    private T data;  //成功：自定义的数据包

    @ApiModelProperty( value = "成功或失败：重定向地址")
    private String redirectUrl; //成功或失败：重定向地址

    @ApiModelProperty( value = "失败：友好的用户提示消息")
    private String errorMsg; //失败：友好的用户提示消息

    private GTJsonResult() {
        super();
    }

    /**
     * ------------ 构造成功消息 ------------
     */
    public static <T> GTJsonResult<T> instanceSuccessMsg() {
        return instanceSuccessMsg(null);
    }

    public static <T> GTJsonResult<T> instanceSuccessMsg(T data) {
        return instanceSuccessMsg(data, null);
    }

    public static <T> GTJsonResult<T> instanceSuccessMsg(T data, String redirectUrl) {
        GTJsonResult gtJsonResult = new GTJsonResult();
        gtJsonResult.setSuccess(true);
        gtJsonResult.setData(data);
        gtJsonResult.setRedirectUrl(redirectUrl);
        return gtJsonResult;
    }

    /**
     * ------------ 构造失败消息 ------------
     */
    public static GTJsonResult instanceErrorMsg() {
        return instanceErrorMsg(CommonConstant.OPERATE_ERROR);
    }

    public static GTJsonResult instanceErrorMsg(String errorMsg) {
        return instanceErrorMsg(errorMsg, "");
    }

    public static GTJsonResult instanceErrorMsg(String errorMsg, String redirectUrl) {
        GTJsonResult gtJsonResult = new GTJsonResult();
        gtJsonResult.setSuccess(false);
        gtJsonResult.setErrorMsg(errorMsg);
        gtJsonResult.setRedirectUrl(redirectUrl);
        return gtJsonResult;
    }

    /**
     * ------------ getter && setter ------------
     */
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }
}


