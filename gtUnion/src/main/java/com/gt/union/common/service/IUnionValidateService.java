package com.gt.union.common.service;

import com.gt.union.common.exception.ParamException;
import org.springframework.validation.BindingResult;

/**
 * 校验表单参数服务类
 * @author hongjiye
 * Created by Administrator on 2017/8/16 0016.
 */
public interface IUnionValidateService {

	/**
	 * 验证参数
	 * @param result
	 * @throws ParamException
	 */
	public void checkBindingResult(BindingResult result) throws ParamException;
}
