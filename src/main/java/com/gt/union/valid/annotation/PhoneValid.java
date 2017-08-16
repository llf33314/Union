package com.gt.union.valid.annotation;

import com.gt.union.validator.PhoneValidator;
import com.gt.union.validator.StringLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/8/16 0016.
 */
@Constraint(validatedBy = PhoneValidator.class) //具体的实现
@Target( { java.lang.annotation.ElementType.METHOD,
		java.lang.annotation.ElementType.FIELD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface PhoneValid {

	String message() default "手机号有误";

	//下面这两个属性必须添加
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
