package com.gt.union.common.filter;

import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 初始化listener
 * Created by Administrator on 2017/7/25 0025.
 */
@WebListener
public class InitListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		contextEvent.getServletContext().setAttribute("resourceUrl", PropertiesUtil.getResourceUrl());
		contextEvent.getServletContext().setAttribute("articleUrl",PropertiesUtil.getArticleUrl());
		CommonUtil.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(contextEvent.getServletContext()));
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

}
