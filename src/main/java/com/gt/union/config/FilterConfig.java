package com.gt.union.config;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2017/7/20 0020.
 */
@WebFilter(filterName = "unionAuthFilter", urlPatterns = "/*")
public class FilterConfig implements Filter{
    @Override
    public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
        //项目启动时只执行一次
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 可通过servletRequest.getParameter获取相关参数进行判断，
        // 如果通过则执行filterChain.doFilter(servletReqeust, servletResponse)，
        // 否则可通过servletResponse直接返回相关提示信息或不返回
    }

    @Override
    public void destroy() {
    }
}
