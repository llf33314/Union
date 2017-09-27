package com.gt.union.common.annotation.validator;

import com.gt.union.common.annotation.valid.StringLengthValid;
import com.gt.union.common.util.StringUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by Administrator on 2017/8/16 0016.
 */
public class StringLengthValidator implements ConstraintValidator<StringLengthValid, String> {

	private int len;

	@Override
	public void initialize(StringLengthValid stringLengthValid) {
		this.len = stringLengthValid.length();
	}

	@Override
	public boolean isValid(String str, ConstraintValidatorContext constraintValidatorContext) {
		if(StringUtil.isEmpty(str)){
			return true;
		}else{
			if(StringUtil.getStringLength(str) > len){
				return false;
			}
		}
		return true;
	}
}
