package com.gt.union.common.exception;

public class BaseException extends Exception {
	private static final long serialVersionUID = 1L;

	protected String errorLocation; //失败：抛出异常的代码位置，格式为"类名.方法名()"
    protected String errorCausedBy; //失败：抛出异常的原因
	protected String errorMsg; //失败：友好的用户提示消息

	public BaseException(String errorLocation, String errorCausedBy, String errorMsg) {
		super();
		this.errorLocation = errorLocation;
		this.errorCausedBy = errorCausedBy;
		this.errorMsg = errorMsg;
	}

    /**
     * ------------ getter&setter ------------
     */
    public String getErrorLocation() {
        return errorLocation;
    }

    public void setErrorLocation(String errorLocation) {
        this.errorLocation = errorLocation;
    }

    public String getErrorCausedBy() {
        return errorCausedBy;
    }

    public void setErrorCausedBy(String errorCausedBy) {
        this.errorCausedBy = errorCausedBy;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
