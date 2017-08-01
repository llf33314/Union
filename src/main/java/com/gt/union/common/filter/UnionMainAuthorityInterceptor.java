package com.gt.union.common.filter;

import com.gt.union.common.annotation.UnionMainAuthorityAnnotation;
import com.gt.union.common.constant.basic.UnionMainAuthorityConstant;
import com.gt.union.common.util.*;
import com.gt.union.service.basic.IUnionMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/8/1 0001.
 */
public class UnionMainAuthorityInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private IUnionMainService unionMainService;

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
		UnionMainAuthorityAnnotation annotation = handlerMethod.getMethodAnnotation(UnionMainAuthorityAnnotation.class);
		//加了联盟权限注解
		if (annotation!=null) {
			String unionId = request.getParameter("unionId");
			if(CommonUtil.isEmpty(unionId)){
				request.setAttribute("unionMainAuthority", UnionMainAuthorityConstant.UNION_ID_IS_NULL);
			}

		}

		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
							 HttpServletResponse response, Object handler) throws Exception {

		return super.preHandle(request, response, handler);
	}

}
