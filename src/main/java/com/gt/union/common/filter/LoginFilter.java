package com.gt.union.common.filter;

import com.alibaba.fastjson.JSON;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GTJsonResult;

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

	/**
	 * 后台路由地址
	 */
	public static final Map<String, String> routeUrls = new HashMap<String, String>();

    /**
     * 手机端路由地址
     */
	public static final Map<String, String> phoneRouteUrls = new HashMap<String, String>();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

		/*******************************路由列表*********************************/
        /*手机端*/
        phoneRouteUrls.put("/phone/#/index","/phone");
        phoneRouteUrls.put("/phone/#/toTxtract","/phone/#/toTxtract");
        phoneRouteUrls.put("/phone/#/toPayList","/phone/#/toPayList");
        phoneRouteUrls.put("/phone/#/toUnPayList","/phone/#/toUnPayList");
        phoneRouteUrls.put("/phone/#/toTxtract/toDetailList","/phone/#/toTxtract/toDetailList");
        phoneRouteUrls.put("/phone/#/toUnionLogin","/phone/#/toUnionLogin");
        phoneRouteUrls.put("/phone/#/toUnionCard","/phone/#/toUnionCard");

        /*后台*/
		routeUrls.put("/#/my-union","/#/my-union");
		routeUrls.put("/#/business","/#/business");
		routeUrls.put("/#/front","/#/front");
		routeUrls.put("/#/finance","/#/finance");
		/*******************************路由列表*********************************/


        passUrlMap.put("/unionH5Brokerage/login", "/unionH5Brokerage/login");


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
        BusUser busUser = SessionUtils.getLoginUser(req);
        busUser = justForDev(req); //TODO 正式中请注释掉
        if(routeUrls.containsKey(url)){//后台路由地址
            if(busUser == null){
                String script = "<script type='text/javascript'>"
                        + "top.location.href='/user/toLogin.do';"
                        + "</script>";
                response.getWriter().write(script);
                return;
            }
        }

        if(phoneRouteUrls.containsKey(url)){//手机端路由地址
            if(busUser == null){
                res.sendRedirect("/phone/#/toLogin");
                return;
            }
        }
        if (busUser == null) {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSON.toJSONString(GTJsonResult.instanceErrorMsg("请重新登录", ConfigConstant.WXMP_ROOT_URL + "/user/tologin.do")));
            return;
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
    private BusUser justForDev(HttpServletRequest request) {
        BusUser busUser = new BusUser();
        busUser.setId(33);
        busUser.setEndTime(new Date());
        busUser.setPid(BusUserConstant.ACCOUNT_TYPE_UNVALID);
        busUser.setLevel(BusUserConstant.LEVEL_EXTREME);
        SessionUtils.setLoginUser(request, busUser);
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
