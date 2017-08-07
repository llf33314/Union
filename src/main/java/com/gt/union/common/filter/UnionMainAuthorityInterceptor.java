package com.gt.union.common.filter;

import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.util.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/1 0001.
 */
public class UnionMainAuthorityInterceptor extends HandlerInterceptorAdapter {


	@Override
	public void afterCompletion(HttpServletRequest request,
								HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		super.afterCompletion(request, response, handler, ex);
	}

	@Override
	public void postHandle(HttpServletRequest request,
						   HttpServletResponse response, Object handler,
						   ModelAndView modelAndView) throws Exception {

		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
							 HttpServletResponse response, Object handler) throws Exception {
		DaoUtil daoUtil= CommonUtil.getApplicationContext().getBean(DaoUtil.class);
		String unionId = request.getParameter("unionId");
		if(CommonUtil.isNotEmpty(unionId)){
			Integer id = CommonUtil.toInteger(unionId);
				//查询联盟信息
			Map<String,Object> main = daoUtil.queryForMap("select * from t_union_main where id = ? and del_status = ? ",id,0);
			if(CommonUtil.isNotEmpty(main)){
				Integer busId = CommonUtil.toInteger(main.get("bus_id"));
				//TODO 1、判断盟主商家有效期



				//TODO 2、根据商家权限判断判断是否需要判断联盟有效期
				//throw new BusinessException("联盟失效");

			}else {
				throw new BusinessException("联盟不存在");
			}
		}
		return super.preHandle(request, response, handler);
	}

}
