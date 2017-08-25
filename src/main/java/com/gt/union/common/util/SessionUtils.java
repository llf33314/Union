package com.gt.union.common.util;

import com.alibaba.fastjson.JSON;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.entity.common.BusUser;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class SessionUtils {
	private static final org.apache.log4j.Logger log = Logger
			.getLogger(SessionUtils.class);


	//获取用户bus_user   SESSION的值
	public static BusUser getLoginUser(HttpServletRequest request) {
		try {
			Object obj = request.getSession().getAttribute(CommonConstant.SESSION_BUSINESS_KEY);
			if(obj != null){
				return JSON.parseObject(obj.toString(),BusUser.class);
			}else{
				return null;
			}
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	}



	//存入 用户bus_user  的值
	@SuppressWarnings("unchecked")
	public static void setLoginUser(HttpServletRequest request, BusUser busUser) {
		try {
			request.getSession().setAttribute(
					CommonConstant.SESSION_BUSINESS_KEY, JSON.toJSONString(busUser));
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}



	/**
	 * 设置session中的商家id信息
	 *
	 * @param request
	 * @param busId
	 * @return
	 */
	public static void setLoginBusId(HttpServletRequest request,Integer busId) {
		try {
			request.getSession().setAttribute(
					CommonConstant.SESSION_BUSUSER_ID_KEY, busId);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 获取session中的商家id信息
	 *
	 * @param request
	 * @return
	 */
	public static Integer getLoginBusId(HttpServletRequest request) {
		try {
			Object obj = request.getSession().getAttribute(CommonConstant.SESSION_BUSUSER_ID_KEY);
			if(obj != null){
				return (Integer) obj;
			}else{
				return null;
			}
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();

		}
		return null;
	}


}
