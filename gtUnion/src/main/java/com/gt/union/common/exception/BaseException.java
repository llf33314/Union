package com.gt.union.common.exception;

/**
 * 基础异常类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
public class BaseException extends Exception {
    private static final long serialVersionUID = 1L;

    //失败：友好的用户提示消息
    protected String errorMsg;

    public BaseException(String errorMsg) {
        super();
        this.errorMsg = errorMsg;
    }

    /**
     * ------------ getter&setter ------------
     */

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
