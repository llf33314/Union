package com.gt.common.exception;
/**
 * 
 * 
 * @author 李逢喜
 * @version 创建时间：2015年9月7日 下午7:14:20
 * @update zhangmz
 * @updateDate 2017年7月12日19:19:59
 */
public class BusinessException extends BaseException {

	private static final long serialVersionUID = 1L;
	private int code;

	public BusinessException(String msg){
	   super(msg);
	}

	/**
	 * 构建异常类
	 *
	 * @param code 编码
	 * @param msg  消息
	 */
	public BusinessException(int code, String msg) {
		this.code = code;
		this.message = msg;
	}

	public int getCode() {
		return code;
	}
}
