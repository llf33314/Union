package com.gt.union.common.annotation.validator;

import com.gt.union.common.annotation.valid.DoublePrecisionValid;
import com.gt.union.common.util.DoubleUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by Administrator on 2017/8/25 0025.
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
