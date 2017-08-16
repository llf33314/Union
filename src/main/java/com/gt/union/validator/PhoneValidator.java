package com.gt.union.validator;

import com.gt.union.common.util.StringUtil;
import com.gt.union.valid.annotation.PhoneValid;
import com.gt.union.valid.annotation.StringLengthValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/16 0016.
 */
public class PhoneValidator implements ConstraintValidator<PhoneValid, String> {


	@Override
	public void initialize(PhoneValid phoneValid) {
	}

	@Override
	public boolean isValid(String str, ConstraintValidatorContext constraintValidatorContext) {
		if(StringUtil.isEmpty(str)){
			return true;
		}
		String regex = "^1[3|4|5|6|7|8][0-9][0-9]{8}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		boolean rs = matcher.matches();
		return rs;
	}
}
