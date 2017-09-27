package com.gt.union.common.exception;

public class BaseException extends Exception {
	private static final long serialVersionUID = 1L;

	protected String errorMsg; //失败：友好的用户提示消息

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
