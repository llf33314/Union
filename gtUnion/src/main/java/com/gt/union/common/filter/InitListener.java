package com.gt.union.common.filter;

import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 监听器初始化
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
@WebListener
public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
        contextEvent.getServletContext().setAttribute("resourceUrl", PropertiesUtil.getResourceUrl());
        CommonUtil.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(contextEvent.getServletContext()));
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {

    }

}
