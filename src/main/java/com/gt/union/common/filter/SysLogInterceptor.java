/**
 * 
 */
package com.gt.common.filter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.gt.common.annotation.SysLogAnnotation;
import com.gt.dao.util.DaoUtil;
import com.gt.entity.user.BusUser;
import com.gt.util.CommonUtil;
import com.gt.util.DateTimeKit;
import com.gt.util.IPKit;
import com.gt.util.KeysUtil;

/**
 * 
 * 功能日志拦截器
 * 
 * @author lifengxi
 *
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
		if(!handler.getClass().getName().endsWith("DwrController")){
			HandlerMethod handlerMethod=(HandlerMethod) handler;
			SysLogAnnotation annotation=handlerMethod.getMethodAnnotation(SysLogAnnotation.class);
			if (annotation!=null) {
				DaoUtil daoUtil=CommonUtil.getApplicationContext().getBean(DaoUtil.class);
				Map<String, Object> logObj=new HashMap<String, Object>();
				BusUser user=CommonUtil.getLoginUser(request);
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
				String date=DateTimeKit.getDateTime(DateTimeKit.DEFAULT_DATE_FORMAT_YYYYMM);
				daoUtil.saveObjectByMap("", "t_bus_log_"+date, logObj);
			}
		super.postHandle(request, response, handler, modelAndView);
		}
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
