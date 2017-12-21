package com.gt.union.common.exception;

/**
 * 业务异常类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
public class BusinessException extends BaseException {
    private static final long serialVersionUID = 1L;

    public BusinessException(String errorMsg) {
        super(errorMsg);
    }
}
