package com.gt.union.common.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Created by Administrator on 2017/7/25 0025.
 */
@WebFilter(filterName = "unionAuthFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {


	@Override
	public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
		//项目启动时只执行一次
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		// 可通过servletRequest.getParameter获取相关参数进行判断，
		// 如果通过则执行filterChain.doFilter(servletReqeust, servletResponse)，
		// 否则可通过servletResponse直接返回相关提示信息或不返回
		//设置允许跨域
       /* HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");*/
		filterChain.doFilter(servletRequest, servletResponse);//默认不处理
	}

	@Override
	public void destroy() {
	}
}
