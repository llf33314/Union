package com.gt.union.common.annotation.validator;

import com.gt.union.common.annotation.valid.StringLengthValid;
import com.gt.union.common.util.StringUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 字符串长度校验注解实现类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
public class StringLengthValidator implements ConstraintValidator<StringLengthValid, String> {

    private int len;

    @Override
    public void initialize(StringLengthValid stringLengthValid) {
        this.len = stringLengthValid.length();
    }

    @Override
    public boolean isValid(String str, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtil.isEmpty(str)) {
            return true;
        } else {
            if (StringUtil.getStringLength(str) > len) {
                return false;
            }
        }
        return true;
    }
}
