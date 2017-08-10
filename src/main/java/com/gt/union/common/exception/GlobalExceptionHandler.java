package com.gt.union.common.exception;

import com.gt.union.common.response.GTJsonResult;
import org.apache.log4j.Logger;
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

	private Logger logger = Logger.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(value = BaseException.class)
	@ResponseBody
	public String defaultErrorHandler(HttpServletRequest req, BaseException e) throws Exception {
		logger.error(e.getMessage(),e);
		return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
	}

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public String defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
		logger.error(e.getMessage(),e);
		return GTJsonResult.instanceErrorMsg("系统错误").toString();
	}

}
