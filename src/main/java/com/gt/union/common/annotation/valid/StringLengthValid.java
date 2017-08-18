package com.gt.union.common.annotation.valid;


import com.gt.union.common.annotation.validator.StringLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/8/16 0016.
 */
@Constraint(validatedBy = StringLengthValidator.class) //具体的实现
@Target( { java.lang.annotation.ElementType.METHOD,
		java.lang.annotation.ElementType.FIELD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface StringLengthValid {

	String message() default "内容长度有误";

	int length() default 0;

	//下面这两个属性必须添加
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
