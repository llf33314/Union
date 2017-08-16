package com.gt.union.service.common.impl;

import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.service.common.UnionInvalidService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Created by Administrator on 2017/8/16 0016.
 */
@Service
public class UnionInvalidServiceImpl implements UnionInvalidService {

	@Override
	public void invalidParameter(BindingResult result) throws ParamException {
		if (result.hasErrors()) {
			List<ObjectError> errorList = result.getAllErrors();
			for (ObjectError error : errorList) {
				throw new ParamException("", "参数错误", error.getDefaultMessage());
			}
		}
	}
}
