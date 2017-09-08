package com.gt.union.common.exception;

import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.response.GTJsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 统一全局异常处理类
 * Created by Administrator on 2017/7/24 0024.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public String defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
		logger.error("系统异常", e);
		return GTJsonResult.instanceErrorMsg(CommonConstant.SYS_ERROR).toString();
	}

}
