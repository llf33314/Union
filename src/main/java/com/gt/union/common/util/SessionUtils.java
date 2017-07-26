package com.gt.union.common.util;

import com.gt.union.common.constant.CommonConst;
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
			Object obj = request.getSession().getAttribute(CommonConst.SESSION_BUSINESS_KEY);
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
					CommonConst.SESSION_BUSINESS_KEY, busUser);
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


}
