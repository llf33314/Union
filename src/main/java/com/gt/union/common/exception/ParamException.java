package com.gt.union.common.exception;

public class ParamException extends BaseException {
    private static final long serialVersionUID = 1L;

	public ParamException(String errorLocation, String errorCausedBy, String errorMsg) {
        super(errorLocation, errorCausedBy, errorMsg);
    }

}
