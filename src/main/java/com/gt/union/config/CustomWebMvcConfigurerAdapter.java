package com.gt.union.config;

import com.gt.union.common.filter.SysLogInterceptor;
import com.gt.union.common.filter.UnionMainAuthorityInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 拦截器配置
 * Created by Administrator on 2017/7/25 0025.
 */
@Configuration
public class CustomWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new UnionMainAuthorityInterceptor()).addPathPatterns("/**");  //对来自/** 这个链接来的请求进行拦截
		registry.addInterceptor(new SysLogInterceptor()).addPathPatterns("/**");  //对来自/** 这个链接来的请求进行拦截
	}
}
