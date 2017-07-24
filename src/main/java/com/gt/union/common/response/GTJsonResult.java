package com.gt.union.common.response;

import com.alibaba.fastjson.JSON;
import com.gt.union.common.response.GTCommonMessageEnum;

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

    /* true 成功 false 失败 */
    private boolean success;
    /*返回定义的代码 0 失败 1 成功*/
    private int code;
    /*返回定义的消息*/
    private String message;
    /*返回的数据包*/
    private T data;
    /*重定向地址*/
    private String redirectUrl;

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
        return GTJsonResult.instanceSuccessMsg(true, data, redirectUrl);
    }

    /**
     * 构造成功消息
     * @param code 编码
     * @param message  消息
     * @param data 数据包
     * @return GTJsonResult
     */
    public static <T> GTJsonResult<T> instanceSuccessMsgCode(int code, String message, T data) {
        GTJsonResult<T> response = new GTJsonResult<>();
        response.setData(data);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    /**
     * 构造成功消息
     * 设定 重定向地址
     *
     * @param success     是否成功
     * @param data        T 泛型
     * @param redirectUrl 重定向地址
     * @return GTJsonResult<T>
     */
    public static <T> GTJsonResult<T> instanceSuccessMsg(boolean success, T data, String redirectUrl) {
        GTJsonResult<T> response = new GTJsonResult<>();
        response.setSuccess(success);
        response.setData(data);
        response.setCode(GTCommonMessageEnum.SUCCESS.getCode());
        response.setMessage(GTCommonMessageEnum.SUCCESS.getMessage());
        response.setRedirectUrl(redirectUrl);
        return response;
    }

    /**
     * 默认方法 构造错误消息
     *
     * @return GTJsonResult
     */
    public static GTJsonResult instanceErrorMsg() {
        return GTJsonResult.instanceErrorMsg(GTCommonMessageEnum.FAIL.getCode(), GTCommonMessageEnum.FAIL.getMessage());
    }

    /**
     * 默认方法 构造错误消息
     * @param msg 错误消息(定制特定消息)
     * @return GTJsonResult
     */
    public static GTJsonResult instanceErrorMsg(String msg) {
        return GTJsonResult.instanceErrorMsg(GTCommonMessageEnum.FAIL.getCode(), msg);
    }

    /**
     * 构造错误消息
     *
     * @param code 代码
     * @param message 描述
     * @return GTJsonResult
     */
    public static GTJsonResult instanceErrorMsg(int code, String message) {
        return GTJsonResult.instanceErrorMsg(code, message, null);
    }

    /**
     * 构造错误消息
     *
     * @param code 代码
     * @param redirectUrl 重定向地址
     * @return GTJsonResult
     */
    public static GTJsonResult instanceErrorMsg(int code, String message, String redirectUrl) {
        GTJsonResult response = new GTJsonResult<>();
        response.setCode(code);
        response.setMessage(message);
        response.setRedirectUrl(redirectUrl);
        return response;
    }

    /**
     * 转换为Json 数据
     *
     * @param jsonResult GTJsonResult<T>
     *
     * @return Json
     */
    public static String convertToJson(GTJsonResult jsonResult) {
        return JSON.toJSONString(jsonResult);
    }

    public static void main(String[] args) {
        GTJsonResult jsonResultErrorMsg = GTJsonResult.instanceErrorMsg(GTCommonMessageEnum.FAIL.getCode(), GTCommonMessageEnum.FAIL.getMessage());
        System.out.println("jsonResultErrorMsg is " + GTJsonResult.convertToJson(jsonResultErrorMsg));

        GTJsonResult<String> jsonResultSuccessMsg = GTJsonResult.instanceSuccessMsg("{aaa:bbb}");


        System.out.println("jsonResultSuccessMsg is " + JSON.toJSONString(jsonResultSuccessMsg));
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

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
        return "GTJsonResult{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", redirectUrl='" + redirectUrl + '\'' +
                '}';
    }
}


