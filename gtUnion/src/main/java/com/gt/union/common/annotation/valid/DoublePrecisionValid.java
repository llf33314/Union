package com.gt.union.common.annotation.valid;

import com.gt.union.common.annotation.validator.DoublePrecisionValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数字小数点长度校验注解
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
@Constraint(validatedBy = DoublePrecisionValidator.class)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DoublePrecisionValid {
    String message() default "数字格式有误";

    //整数长度
    int integer() default 0;

    //小数长度
    int decimal() default 0;

    //下面这两个属性必须添加
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
