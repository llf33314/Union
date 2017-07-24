package com.gt.union.common.exception;


import com.gt.union.common.util.CommonUtil;

/**
 * 
 * 基础异常类
 * @author 李逢喜
 * @version 创建时间：2015年9月7日 下午7:14:20
 * 
 */
public class BaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String message;
	private boolean result = false;
	
	
	public BaseException() {
		super();
	}


	public BaseException(boolean result, String message) {
		super();
		this.result = result;
		this.message = message;
	}

	/**
	 * 修订msg 。
	 *
	 * @param errMsg
	 */
	public BaseException(String errMsg){
		super(errMsg);
		this.message = errMsg;
	}

	public boolean getResult() {

		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getMessage() {
		return CommonUtil.isEmpty(message) ? "操作失败，系统异常！" : message;
	}

    public void setMessage(String message) {
        this.message = message;
    }
}
