package com.gt.union.common.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.common.constant.CommonConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果格式
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
@ApiModel(value = "GTJsonResult", description = "响应结果实体")
@Data
public class GtJsonResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * true:成功;false:失败-由方法标签控制
     */
    @ApiModelProperty(value = "true:成功;false:失败")
    private boolean success;

    /**
     * 成功：自定义的数据包
     */
    @ApiModelProperty(value = "成功返回的具体数据")
    private T data;

    /**
     * 成功或失败：重定向地址
     */
    @ApiModelProperty(value = "成功或失败：重定向地址")
    private String redirectUrl;

    /**
     * 失败：友好的用户提示消息
     */
    @ApiModelProperty(value = "失败：友好的用户提示消息")
    private String errorMsg;

    private GtJsonResult() {
        super();
    }

    /**
     * ------------ 构造成功消息 ------------
     */
    public static <T> GtJsonResult<T> instanceSuccessMsg() {
        return instanceSuccessMsg(null);
    }

    public static <T> GtJsonResult<T> instanceSuccessMsg(T data) {
        return instanceSuccessMsg(data, null);
    }

    public static <T> GtJsonResult<T> instanceSuccessMsg(T data, String redirectUrl) {
        GtJsonResult gtJsonResult = new GtJsonResult();
        gtJsonResult.setSuccess(true);
        gtJsonResult.setData(data);
        gtJsonResult.setRedirectUrl(redirectUrl);
        return gtJsonResult;
    }

    /**
     * ------------ 构造失败消息 ------------
     */
    public static GtJsonResult instanceErrorMsg() {
        return instanceErrorMsg(CommonConstant.OPERATE_ERROR);
    }

    public static GtJsonResult instanceErrorMsg(String errorMsg) {
        return instanceErrorMsg(errorMsg, "");
    }

    public static GtJsonResult instanceErrorMsg(String errorMsg, String redirectUrl) {
        GtJsonResult gtJsonResult = new GtJsonResult();
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
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
