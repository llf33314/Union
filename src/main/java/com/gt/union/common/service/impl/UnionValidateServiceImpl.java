package com.gt.union.common.service.impl;

import com.gt.union.common.exception.ParamException;
import com.gt.union.common.service.IUnionValidateService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Created by Administrator on 2017/8/16 0016.
 */
@Service
public class UnionValidateServiceImpl implements IUnionValidateService {

	@Override
	public void checkBindingResult(BindingResult result) throws ParamException {
		if (result.hasErrors()) {
			List<ObjectError> errorList = result.getAllErrors();
			for (ObjectError error : errorList) {
				throw new ParamException(error.getDefaultMessage());
			}
		}
	}
}
