package com.gt.union.common.util;

import com.gt.union.common.exception.ParamException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

public class ParamValidatorUtil {

    /**
     * 参数验证
     * @param result
     * @throws ParamException
     */
    public static void checkBindingResult(BindingResult result) throws ParamException {
        if (result.hasErrors()) {
            List<ObjectError> errorList = result.getAllErrors();
            for (ObjectError error : errorList) {
                throw new ParamException(error.getDefaultMessage());
            }
        }
    }
}
