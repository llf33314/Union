package com.gt.union.common.filter;

import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.Member;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.finance.verifier.entity.UnionVerifier;
import com.gt.union.h5.brokerage.vo.H5BrokerageUser;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 访问过滤器
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
@WebFilter(filterName = "loginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {

    /**
     * 不需要登录的url
     */
    private final Map<String, String> passUrlMap = new HashMap<>();

    /**
     * 不需要过滤的文件类型
     */
    private final List<String> passSuffixList = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        passUrlMap.put("/h5Brokerage/loginSign", "/h5Brokerage/loginSign");
        passUrlMap.put("/h5Brokerage/loginStatus", "/h5Brokerage/loginStatus");
        passUrlMap.put("/h5Brokerage/login/phone", "/h5Brokerage/login/phone");
        passUrlMap.put("/api/sms/1", "/api/sms/1");
        passUrlMap.put("/api/sms/3", "/api/sms/3");
        passUrlMap.put("/api/sms/4", "/api/sms/4");
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
        if (isPassSuffixRequest(url) || isPassUrl(url) || isMobileRequest(url) || isApiRequest(url) || isSwaggerUIRequest(url) || isFrontRequest(url)) {
            chain.doFilter(request, response);
            return;
        }

        //(3)判断是否已有登录信息
        // （3-1）佣金平台
        if (url.indexOf("h5Brokerage") > -1) {
            doH5BrokerageFilter(req, res, chain);
        } else if (url.indexOf("wxAppCard") > -1) {
            doWxAppCardFilter(req, res, chain, url);
        } else {
            doWebFilter(req, res, chain, url);
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * 判断是否是不需要过滤的文件类型
     *
     * @param url 请求地址
     * @return boolean
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
     * @param url 请求地址
     * @return boolean
     */
    private boolean isPassUrl(String url) {
        return passUrlMap.containsKey(url);
    }

    /**
     * 判断是否是移动端请求
     *
     * @param url 请求地址
     * @return boolean
     */
    private boolean isMobileRequest(String url) {
        return url.indexOf("79B4DE7C") > -1 ? true : false;
    }

    /**
     * 判断是否是内部系统接口请求
     *
     * @param url 请求地址
     * @return boolean
     */
    private boolean isApiRequest(String url) {
        return url.indexOf("8A5DA52E") > -1 ? true : false;
    }

    /**
     * 判断是否是swaggerUI请求
     *
     * @param url 请求地址
     * @return boolean
     */
    private boolean isSwaggerUIRequest(String url) {
        return (url.indexOf("v2") > -1 || url.indexOf("swagger") > -1) ? true : false;
    }

    /**
     * 是否前端资源请求
     *
     * @param url 请求对象
     * @return boolean
     */
    private boolean isFrontRequest(String url) {
        return StringUtil.isNotEmpty(url) && (url.contains("brokeragePhone") || url.contains("cardPhone"));
    }

    private void doH5BrokerageFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        BusUser busUser = SessionUtils.getUnionBus(req);
        // 开发调试
        if (busUser == null && ConfigConstant.IS_DEV == CommonConstant.COMMON_YES) {
            justForDev(req);
            justForH5BrokerageDev(req);
            chain.doFilter(req, res);
            return;
        }
        // 发布
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(req);
        if (h5BrokerageUser == null) {
            if (busUser == null) {
                res.getWriter().write(GtJsonResult.instanceSuccessMsg(null, PropertiesUtil.getUnionUrl() + "/brokeragePhone/#/" + "toLogin").toString());
            } else if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                res.getWriter().write(GtJsonResult.instanceErrorMsg(CommonConstant.BUS_PARENT_TIP).toString());
            } else {
                h5BrokerageUser = new H5BrokerageUser();
                h5BrokerageUser.setBusUser(busUser);

                UnionVerifier adminVerifier = new UnionVerifier();
                adminVerifier.setBusId(busUser.getId());
                adminVerifier.setEmployeeName("管理员");
                h5BrokerageUser.setVerifier(adminVerifier);

                UnionSessionUtil.setH5BrokerageUser(req, h5BrokerageUser);

                chain.doFilter(req, res);
            }
        } else {
            if(busUser != null){
                if(!h5BrokerageUser.getBusUser().getId().equals(busUser.getId())){

                    h5BrokerageUser.setBusUser(busUser);

                    UnionVerifier adminVerifier = new UnionVerifier();
                    adminVerifier.setBusId(busUser.getId());
                    adminVerifier.setEmployeeName("管理员");
                    h5BrokerageUser.setVerifier(adminVerifier);

                    UnionSessionUtil.setH5BrokerageUser(req, h5BrokerageUser);
                }
            }else {
                res.getWriter().write(GtJsonResult.instanceSuccessMsg(null, PropertiesUtil.getUnionUrl() + "/brokeragePhone/#/" + "toLogin").toString());
                return;
            }
            chain.doFilter(req, res);
        }
    }

    private void doWxAppCardFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain, String url) throws IOException, ServletException {
        //调试手机端，设置member数据
        if (ConfigConstant.IS_DEV == CommonConstant.COMMON_YES) {
            Member member = SessionUtils.getLoginMember(req, 33);
            if (member == null) {
                member = new Member();
                member.setId(998);
                member.setPhone("15986670850");
                member.setBusid(33);
                SessionUtils.setLoginMember(req, member);
            }
        }
        chain.doFilter(req, res);
    }

    private void doWebFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain, String url) throws IOException, ServletException {
        // 后台
        BusUser busUser = SessionUtils.getLoginUser(req);
        if (busUser == null && ConfigConstant.IS_DEV == CommonConstant.COMMON_YES) {
            justForDev(req);
            chain.doFilter(req, res);
            return;
        }
        if (busUser == null) {
            res.getWriter().write(GtJsonResult.instanceSuccessMsg(null, PropertiesUtil.getWxmpUrl() + "/user/tologin.do").toString());
        } else {
            chain.doFilter(req, res);
        }
    }

    /**
     * 开发测试专用，正式中请注释掉
     *
     * @param request 请求对象
     * @return BusUser
     */
    private BusUser justForDev(HttpServletRequest request) {
        BusUser busUser = new BusUser();
        busUser.setId(33);
        busUser.setEndTime(new Date());
        busUser.setPhone(ConfigConstant.DEVELOPER_PHONE);
        busUser.setPid(BusUserConstant.ACCOUNT_TYPE_UNVALID);
        busUser.setLevel(BusUserConstant.LEVEL_EXTREME);
        SessionUtils.setLoginUser(request, busUser);
        return busUser;
    }

    /**
     * 开发测试专用，正式中请注释掉
     *
     * @param request 请求对象
     * @return BusUser
     */
    private H5BrokerageUser justForH5BrokerageDev(HttpServletRequest request) {
        H5BrokerageUser h5BrokerageUser = new H5BrokerageUser();
        BusUser busUser = justForDev(request);
        h5BrokerageUser.setBusUser(busUser);

        UnionVerifier verifier = new UnionVerifier();
        verifier.setCreateTime(DateUtil.getCurrentDate());
        verifier.setDelStatus(CommonConstant.DEL_STATUS_NO);
        verifier.setBusId(busUser.getId());
        verifier.setPhone(busUser.getPhone());
        h5BrokerageUser.setVerifier(verifier);

        UnionSessionUtil.setH5BrokerageUser(request, h5BrokerageUser);
        return h5BrokerageUser;
    }
}
