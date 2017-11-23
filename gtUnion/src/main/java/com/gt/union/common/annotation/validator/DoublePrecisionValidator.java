package com.gt.union.common.annotation.validator;

import com.gt.union.common.annotation.valid.DoublePrecisionValid;
import com.gt.union.common.util.DoubleUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 数字小数点长度校验注解实现类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
public class DoublePrecisionValidator implements ConstraintValidator<DoublePrecisionValid, Double> {
    private int integer;
    private int decimal;

    @Override
    public void initialize(DoublePrecisionValid doubleLengthValid) {
        this.integer = doubleLengthValid.integer();
        this.decimal = doubleLengthValid.decimal();
    }

    @Override
    public boolean isValid(Double aDouble, ConstraintValidatorContext constraintValidatorContext) {
        return aDouble != null && DoubleUtil.checkPrecision(aDouble, integer, decimal);
    }
}
