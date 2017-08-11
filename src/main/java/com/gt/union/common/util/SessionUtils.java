package com.gt.union.common.util;

import com.gt.union.common.constant.CommonConstant;
import com.gt.union.entity.common.BusUser;
import com.gt.union.entity.common.Member;
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
				return (BusUser) obj;
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
					CommonConstant.SESSION_BUSINESS_KEY, busUser);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 设置session中的商家member会员信息
	 *
	 * @param request
	 * @return
	 */
	public static void setLoginMember(HttpServletRequest request,Member member) {
		try {
			request.getSession().setAttribute(
					"member", member);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 获取session中的商家会员信息
	 *
	 * @param request
	 * @return
	 */
	public static Member getLoginMember(HttpServletRequest request) {
		try {
			Object obj = request.getSession().getAttribute("member");
			if(obj != null){
				return (Member) obj;
			}else{
				return null;
			}
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();

		}
		return null;
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
