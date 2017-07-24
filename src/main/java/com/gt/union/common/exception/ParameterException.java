package com.gt.common.exception;
/**
 * 
 * 参数错误异常
 * @author 李逢喜
 * @version 创建时间：2015年9月7日 下午7:14:20
 * 
 */
public class ParameterException extends BaseException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param code 错误代码
	 */
	 public ParameterException(String code){
		 super(code);
	 }  
	 
}
