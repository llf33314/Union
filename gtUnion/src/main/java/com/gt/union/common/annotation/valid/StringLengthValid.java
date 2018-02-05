package com.gt.union.common.annotation.valid;


import com.gt.union.common.annotation.validator.StringLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 字符串长度校验注解
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
@Constraint(validatedBy = StringLengthValidator.class)
@Target({java.lang.annotation.ElementType.METHOD,
    java.lang.annotation.ElementType.FIELD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface StringLengthValid {

    String message() default "内容长度有误";

    int length() default 0;

    //下面这两个属性必须添加
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
