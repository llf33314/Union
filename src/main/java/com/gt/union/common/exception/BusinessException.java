package com.gt.union.common.exception;

public class BusinessException extends BaseException {
    private static final long serialVersionUID = 1L;

    public BusinessException(String errorLocation, String errorCausedBy, String errorMsg) {
        super(errorLocation, errorCausedBy, errorMsg);
    }
}
