/**
 * 
 */
package com.gt.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lifengxi
 *
 * @date 2015-6-23
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SysLogAnnotation {
	/**
	 * 功能模块
	 * @return
	 */
	public String op_function() default "" ;
	
	/**
	 * 操作类型(分为 1:功能日志、2:错误日志)
	 * @return
	 */
	public String log_type() default "1" ;
	
	/**
	 * 描述
	 * @return
	 */
	public String description() default "" ;
	
}
