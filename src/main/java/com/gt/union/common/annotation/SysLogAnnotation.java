/**
 * 
 */
package com.gt.union.common.annotation;

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
	 * 功能模块 1、查询 2、新增 3、修改 4、删除 5、其他
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
