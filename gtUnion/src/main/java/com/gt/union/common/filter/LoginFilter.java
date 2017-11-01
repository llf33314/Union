package com.gt.union.common.filter;

import com.alibaba.fastjson.JSON;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 访问过滤器
 * Created by Administrator on 2017/7/25 0025.
 */
@WebFilter(filterName = "loginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {

    //不需要登录的url
    private final Map<String, String> passUrlMap = new HashMap<>();

    //不需要过滤的文件类型
    private final List<String> passSuffixList = new ArrayList<>();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        passUrlMap.put("/unionH5Brokerage/login", "/unionH5Brokerage/login");
        passUrlMap.put("/unionH5Brokerage/loginSign", "/unionH5Brokerage/loginSign");
        passSuffixList.add(".js");
        passSuffixList.add(".css");
        passSuffixList.add(".gif");
        passSuffixList.add(".png");
        passSuffixList.add(".jpg");
        passSuffixList.add(".ico");
        passSuffixList.add(".html");
        passSuffixList.add(".dwr");
        passSuffixList.add(".mp3");
        passSuffixList.add(".txt");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //(1)设置跨域
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, OPTIONS");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, api_key, Authorization");
        //(2)判断是否是不需要权限的请求地址
        String url = req.getRequestURI();
        if (isPassSuffixRequest(url) || isPassUrl(url) || isMobileRequest(url) || isApiRequest(url) || isSwaggerUIRequest(url)) {
            chain.doFilter(request, response);
            return;
        }
        //(3)判断是否已有登录信息
        if (url.indexOf("unionH5Brokerage") > -1 || url.indexOf("brokeragePhone") > -1) {
            BusUser user = SessionUtils.getUnionBus(req);
            if (user == null) {
                if ("/brokeragePhone/".equals(url)) {
                    chain.doFilter(request, response);
                    return;
                } else if (url.indexOf("unionH5Brokerage") > -1) {
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(GTJsonResult.instanceSuccessMsg(null, PropertiesUtil.getUnionUrl() + "/brokeragePhone/#/" + "toLogin").toString());
                    return;
                }
            }
        } else {
            BusUser busUser = SessionUtils.getLoginUser(req);
            if ("/cardPhone/".equals(url) || "/cardPhone".equals(url)) {//
                chain.doFilter(request, response);
                return;
            }
            if ("dev".equals(PropertiesUtil.getProfiles())) {
                busUser = justForDev(req, busUser);
            }
            if (busUser == null) {
                if ("/unionMain/".equals(url)) {
                    String wxmpLoginUrl = PropertiesUtil.getWxmpUrl() + "/user/tologin.do";
                    String script = "<script type='text/javascript'>"
                            + "location.href='" + wxmpLoginUrl + "';"
                            + "</script>";
                    response.getWriter().write(script);
                    return;
                } else {
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(GTJsonResult.instanceSuccessMsg(null, PropertiesUtil.getWxmpUrl() + "/user/tologin.do").toString());
                    return;
                }
            }
        }
        //(4)通过
        chain.doFilter(request, response);
    }

    /**
     * 开发测试专用，正式中请注释掉
     *
     * @param request
     * @return
     */
    private BusUser justForDev(HttpServletRequest request, BusUser busUser) {
        if (busUser == null) {
            busUser = new BusUser();
            busUser.setId(33);
            busUser.setEndTime(new Date());
            busUser.setPid(BusUserConstant.ACCOUNT_TYPE_UNVALID);
            busUser.setLevel(BusUserConstant.LEVEL_EXTREME);
            SessionUtils.setLoginUser(request, busUser);
        }
        return busUser;
    }


    @Override
    public void destroy() {
    }

    /**
     * 判断是否是不需要过滤的文件类型
     *
     * @param url
     * @return
     */
    private boolean isPassSuffixRequest(String url) {
        for (String passSuffix : passSuffixList) {
            if (url.endsWith(passSuffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是不需要登录的url
     *
     * @param url
     * @return
     */
    private boolean isPassUrl(String url) {
        return passUrlMap.containsKey(url);
    }

    /**
     * 判断是否是移动端请求
     *
     * @param url
     * @return
     */
    private boolean isMobileRequest(String url) {
        return url.indexOf("79B4DE7C") > -1 ? true : false;
    }

    /**
     * 判断是否是内部系统接口请求
     *
     * @param url
     * @return
     */
    private boolean isApiRequest(String url) {
        return url.indexOf("8A5DA52E") > -1 ? true : false;
    }

    /**
     * 判断是否是swaggerUI请求
     *
     * @param url
     * @return
     */
    private boolean isSwaggerUIRequest(String url) {
        return (url.indexOf("v2") > -1 || url.indexOf("swagger") > -1) ? true : false;
    }
}
