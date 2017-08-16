package com.gt.union.service.common;

import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Created by Administrator on 2017/8/16 0016.
 */
public interface UnionInvalidService {

	/**
	 * valid验证参数
	 * @param result
	 * @throws ParamException
	 */
	public void invalidParameter( BindingResult result ) throws ParamException;
}
