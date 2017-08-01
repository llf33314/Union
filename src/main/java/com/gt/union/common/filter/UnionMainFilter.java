package com.gt.union.common.filter;

import com.alibaba.fastjson.JSON;
import com.gt.union.common.response.GTJsonResult;
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
import java.util.HashMap;
import java.util.Map;

/**
 * 联盟的过滤器 处理联盟的有效性
 * Created by Administrator on 2017/7/26 0026.
 */
@WebFilter(filterName = "unionMainFilter", urlPatterns = "/*")
public class UnionMainFilter implements Filter {

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
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE");
		httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
		httpServletResponse.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		/*HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		BusUser busUser = SessionUtils.getLoginUser(req);
		String url = ((HttpServletRequest) request).getRequestURI();//获取请求路径
		if(url.equals("/")){
			res.sendRedirect("http://www."+ PropertiesUtil.getDomainDf());
		}else if(url.indexOf("79B4DE7C")>-1){//移动端
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
			response.setCharacterEncoding("UTF-8");
			String script = "<script type='text/javascript'>"
					+ "top.location.href="+PropertiesUtil.getWxmpUrl()+"'/user/tologin.do';"
					+ "</script>";
			if(isAjax(req)){//TODO 过滤器ajax请求未登录处理
				Map map = new HashMap<>();
				map.put("timeout", "连接超时，请重新登录！");
				response.getWriter().write(JSON.toJSONString(GTJsonResult.));
			}else{
				response.getWriter().write(script);
			}
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
		}*/
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
