package com.gt.union.common.filter;

import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.entity.common.Member;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		//过滤路径
		urls.put("/unionBrokerage/toLogin.do", "/unionBrokerage/toLogin.do");
		urls.put("/unionBrokerage/login.do", "/unionBrokerage/login.do");

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
		res.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
		res.setHeader("Access-Control-Max-Age", "3600");
		res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, api_key, Authorization");
		BusUser busUser = SessionUtils.getLoginUser(req);
		String url = ((HttpServletRequest) request).getRequestURI();//获取请求路径
		if(url.indexOf("79B4DE7C")>-1){//移动端
			Member member= SessionUtils.getLoginMember(req);
			if(CommonUtil.isNotEmpty(member) && member.isPass()){//商家已过期，清空会员登录session
				req.getSession().removeAttribute("member");
				String upGradeUrl=PropertiesUtil.getWxmpUrl() + "/jsp/merchants/user/warning.jsp";
				res.sendRedirect(upGradeUrl);
			}else{
				chain.doFilter(request, response);
			}
		}else if(passSuffixs(url)||passUrl(url)){
			chain.doFilter(request, response);
		}else if (busUser == null) {// 判断到商家没有登录,就跳转到登陆页面
			//TODO 设置默认登录
			busUser = new BusUser();
			busUser.setId(33);
			busUser.setPid(0);
			busUser.setLogin_source(1);
			SessionUtils.setLoginUser(req,busUser);
			response.setCharacterEncoding("UTF-8");
			chain.doFilter(request, response);
			//response.getWriter().write(JSON.toJSONString(GTJsonResult.instanceErrorMsg("请重新登录",PropertiesUtil.getWxmpUrl()+"/user/tologin.do")));
		}else if(busUser != null && busUser.getPid() == 0 && busUser.getLogin_source() != 1){//商家主账号过期,跳转到充值页面
			if(busUser.getDays()<0){
				String upGradeUrl=PropertiesUtil.getWxmpUrl() + "/jsp/merchants/user/pastPage.jsp";
				res.sendRedirect(upGradeUrl);
			}else{
				chain.doFilter(request, response);
			}
		}else if(busUser != null && busUser.getPid() != 0) {//TODO 登录的是子账户，但是主账号过期了跳转到充值页面
			if (busUser.getDays() < 0) {
				if (url.equals("/trading/upGrade.do")) {
					chain.doFilter(request, response);
				} else {
					String upGradeUrl = PropertiesUtil.getWxmpUrl() + "/jsp/merchants/user/pastPage.jsp";
					res.sendRedirect(upGradeUrl);
				}
			} else {
				chain.doFilter(request, response);
			}
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
