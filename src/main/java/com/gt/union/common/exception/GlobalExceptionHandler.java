package com.gt.union.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * 统一全局异常处理类
 * Created by Administrator on 2017/7/24 0024.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 返回错误页面
	 */
	public static final String DEFAULT_ERROR_VIEW = "error";

	/**
	 * 返回手机端错误页面
	 */
	public static final String DEFAULT_PHONE_ERROR_VIEW = "errorPhone";


	@ExceptionHandler(value = BusinessException.class)
	@ResponseBody
	public ResponseEntity defaultErrorHandler(HttpServletRequest req, BusinessException e) throws Exception {
		HashMap<String, Object> body = new HashMap<>();
		body.put("message",e.getMessage());
		body.put("code",0);
		HttpStatus status = getStatus(req);
		return new ResponseEntity(body, status);
	}

	@ExceptionHandler(value = ParameterException.class)
	@ResponseBody
	public ResponseEntity defaultErrorHandler(HttpServletRequest req, ParameterException e) throws Exception {
		HashMap<String, Object> body = new HashMap<>();
		body.put("message",e.getMessage());
		body.put("code",0);
		HttpStatus status = getStatus(req);
		return new ResponseEntity(body, status);
	}



	@ExceptionHandler(value = Exception.class)
	public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.addObject("exception", e);
		mav.addObject("url", req.getRequestURL());
		String requrl = req.getRequestURI();
		mav.setViewName(DEFAULT_ERROR_VIEW);
		if(requrl.indexOf("79B4DE7C") > -1){//手机端
			mav.setViewName(DEFAULT_PHONE_ERROR_VIEW);
		}
		return mav;
	}

	private HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
		if(statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		} else {
			try {
				return HttpStatus.valueOf(statusCode.intValue());
			} catch (Exception var4) {
				return HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}
	}
}
