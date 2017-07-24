package com.gt.union.common.exception;
/**
 * 业务异常类
 * 
 * @author 李逢喜
 * @version 创建时间：2015年9月7日 下午7:14:20
 * @update zhangmz
 * @updateDate 2017年7月12日19:19:59
 */
public class BusinessException extends BaseException {

	private static final long serialVersionUID = 1L;
	private int code;

	public BusinessException(String message){
	   super(message);
	}

	/**
	 * 构建异常类
	 *
	 * @param code 状态码
	 * @param message  消息
	 */
	public BusinessException(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}
}
