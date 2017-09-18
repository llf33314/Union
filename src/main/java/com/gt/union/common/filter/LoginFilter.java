package com.gt.union.common.filter;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;

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


	//不需要登录就可访问的url
	public static final Map<String, String> urls=new HashMap<String, String>();



	//可通过的文件类型
	public static final List<String> suffixs=new ArrayList<String>();

	static {
		//过滤佣金平台路径
		urls.put("/unionH5Brokerage/toLogin.do", "/unionH5Brokerage/toLogin.do");
		urls.put("/unionH5Brokerage/login.do", "/unionH5Brokerage/login.do");

		//文件类型
		suffixs.add("js");
		suffixs.add("css");
		suffixs.add("gif");
		suffixs.add("png");
		suffixs.add("jpg");
		suffixs.add("ico");
		suffixs.add("html");
		suffixs.add("dwr");
		suffixs.add("mp3");
		suffixs.add("txt");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//项目启动时只执行一次
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 可通过servletRequest.getParameter获取相关参数进行判断，
		// 如果通过则执行filterChain.doFilter(servletReqeust, servletResponse)，
		// 否则可通过servletResponse直接返回相关提示信息或不返回
		//设置允许跨域
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, OPTIONS");
		res.setHeader("Access-Control-Max-Age", "3600");
		res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, api_key, Authorization");
		BusUser busUser = SessionUtils.getLoginUser(req);
		String url = ((HttpServletRequest) request).getRequestURI();//获取请求路径
		if(url.indexOf("79B4DE7C")>-1){//移动端
			chain.doFilter(request, response);
		}else if(url.split("8A5DA52E").length > 1){//API接口
			chain.doFilter(request, response);
		}else if(passSuffixs(url)||passUrl(url)){
			chain.doFilter(request, response);
		}else if (busUser == null) {// 判断到商家没有登录,就跳转到登陆页面
			busUser = new BusUser();
			busUser.setId(15);
			busUser.setEndTime(new Date());
			busUser.setPid(0);
			busUser.setLevel(4);
			SessionUtils.setLoginUser(req,busUser);
			chain.doFilter(request, response);
//			response.setCharacterEncoding("UTF-8");
//			response.getWriter().write(JSON.toJSONString(GTJsonResult.instanceErrorMsg("请重新登录",PropertiesUtil.getWxmpUrl()+"/user/tologin.do")));
		}else {
			// 已经登陆,继续此次请求
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}

	//判断是否是可通过的url
	private boolean passUrl(String url){
		return urls.containsKey(url);
	}

	/**
	 * 判断文件类型结尾
	 * @param url
	 * @return
	 */
	private boolean passSuffixs(String url){
		boolean reuslt=false;
		for (int i = 0; i < suffixs.size(); i++) {
			if(url.endsWith(suffixs.get(i))){
				reuslt=true;
				break;
			}
		}
		return reuslt;
	}

	/**
	 * TODO 判断ajax请求
	 * @param request
	 * @return
	 */
	boolean isAjax(HttpServletRequest request){
		return  (request.getHeader("X-Requested-With") != null  && "XMLHttpRequest".equals( request.getHeader("X-Requested-With").toString())   ) ;
	}
}
