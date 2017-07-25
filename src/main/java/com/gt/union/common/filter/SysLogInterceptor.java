/**
 * 
 */
package com.gt.union.common.filter;

import com.alibaba.fastjson.JSONObject;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.util.*;
import com.gt.union.dao.DaoUtil;
import com.gt.union.entity.common.BusUser;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 功能日志拦截器
 * @date 2015-6-23
 */
public class SysLogInterceptor extends HandlerInterceptorAdapter {

	@Override
	public void afterCompletion(HttpServletRequest request,
								HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		super.afterCompletion(request, response, handler, ex);
	}

	@Override
	public void postHandle(HttpServletRequest request,
						   HttpServletResponse response, Object handler,
						   ModelAndView modelAndView) throws Exception {
		HandlerMethod handlerMethod=(HandlerMethod) handler;
		SysLogAnnotation annotation=handlerMethod.getMethodAnnotation(SysLogAnnotation.class);
		if (annotation!=null) {
			DaoUtil daoUtil= CommonUtil.getApplicationContext().getBean(DaoUtil.class);
			Map<String, Object> logObj=new HashMap<String, Object>();
			BusUser user= SessionUtils.getLoginUser(request);
			Class<?> bean=handlerMethod.getBeanType();
			String controller=bean.getName();
			if(user!=null){
				logObj.put("opt_person", user.getName());
			}
			logObj.put("opt_controller",controller);
			logObj.put("opt_method", handlerMethod.getMethod().getName());
			logObj.put("opt_desc", annotation.description());
			logObj.put("opt_function", annotation.op_function());
			logObj.put("log_type", annotation.log_type());
			logObj.put("opt_ip", IPKit.getIpAddr(request));
			KeysUtil des = new KeysUtil();
			logObj.put("opt_paramers",des.getEncString(JSONObject.toJSONString(getParamers(request))));
			String date= DateTimeKit.getDateTime(DateTimeKit.DEFAULT_DATE_FORMAT_YYYYMM);
			daoUtil.saveObjectByMap("", "t_bus_log_"+date, logObj);
		}
		super.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request,
							 HttpServletResponse response, Object handler) throws Exception {
		
		return super.preHandle(request, response, handler);
	}


	
	private Map<String, Object> getParamers(HttpServletRequest request){
		Map<String, Object> paramers=new HashMap<String, Object>();
		Enumeration<String> enumeration=request.getParameterNames();
		while(enumeration.hasMoreElements()){
			String key=enumeration.nextElement();
			paramers.put(key, request.getParameter(key));
		}
		return paramers;
	}
}
