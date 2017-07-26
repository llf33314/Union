package com.gt.union.common.response;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 全局JSON格式
 * <pre>
 *      供给 Controller 使用
 *      定制 JSON 消息返回格式
 *      example:
 *      GTJsonResult jsonResultErrorMsg = GTJsonResult.instanceErrorMsg();
 *      GTJsonResult jsonResultSuccessMsg = GTJsonResult.instanceSuccessMsg();
 *      建议采用 {@link GTCommonMessageEnum} 辅助
 *      可定制类似 {@link GTCommonMessageEnum} , 命名方式 GT{XXX}MessageEnum 替换{XXX}
 * </pre>
 * @update 2017/05/26 添加注解@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
 * @create 2017/5/5
 */
public class GTJsonResult<T> implements Serializable {

    /**
     * true:成功;false:失败-由方法标签控制
     */
    private boolean success;
    /**
     * 返回定义的提示消息
     */
    private String message;
    /**
     * 重定向地址
     */
    private String redirectUrl;
    /**
     * 返回定义的数据包
     */
    private T data;


    private GTJsonResult() {
        super();
    }


    /**
     * 构造成功消息
     *
     * @return GTJsonResult<T>
     */
    public static <T> GTJsonResult<T> instanceSuccessMsg() {
        return GTJsonResult.instanceSuccessMsg(null);
    }

    /**
     * 构造成功消息
     *
     * @param data 数据包
     * @return GTJsonResult<T>
     */
    public static <T> GTJsonResult<T> instanceSuccessMsg(T data) {
        return GTJsonResult.instanceSuccessMsg(data, null);
    }

    /**
     * 构造成功消息
     *
     * @param data        T 泛型
     * @param redirectUrl 重定向地址
     * @return GTJsonResult<T>
     */
    public static <T> GTJsonResult<T> instanceSuccessMsg(T data, String redirectUrl) {
        return GTJsonResult.instanceSuccessMsg(data, redirectUrl, GTCommonMessageEnum.SUCCESS.getMessage());
    }

    /**
     * 构造成功消息
     *
     * @param data        T 泛型
     * @param redirectUrl 重定向地址
     * @param message 自定义消息
     * @return GTJsonResult<T>
     */
    public static <T> GTJsonResult<T> instanceSuccessMsg(T data, String redirectUrl, String message) {
        GTJsonResult gtJsonResult = new GTJsonResult();
        gtJsonResult.setSuccess(true);
        gtJsonResult.setData(data);
        gtJsonResult.setRedirectUrl(redirectUrl);
        gtJsonResult.setMessage(message);
        return gtJsonResult;
    }


    /**
     * 默认方法 构造错误消息
     *
     * @return GTJsonResult
     */
    public static GTJsonResult instanceErrorMsg() {
        return GTJsonResult.instanceErrorMsg(GTCommonMessageEnum.FAIL.getMessage());
    }

    /**
     * 默认方法 构造错误消息
     * @param msg 描述
     * @return GTJsonResult
     */
    public static GTJsonResult instanceErrorMsg(String msg) {
        return GTJsonResult.instanceErrorMsg(msg, null);
    }

    /**
     * 构造错误消息
     * @param message 描述
     * @param redirectUrl 重定向地址
     * @return GTJsonResult
     */
    public static GTJsonResult instanceErrorMsg(String message, String redirectUrl) {
        GTJsonResult gtJsonResult = new GTJsonResult();
        gtJsonResult.setSuccess(false);
        gtJsonResult.setMessage(message);
        gtJsonResult.setRedirectUrl(redirectUrl);
        return gtJsonResult;
    }

    /**
     * ------------getter && setter ------------
     */

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}


