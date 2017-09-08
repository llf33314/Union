package com.gt.union.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.entity.BusUser;
import com.gt.union.common.entity.Member;
import com.gt.union.verifier.entity.UnionVerifier;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class SessionUtils {
	private static final Logger log = Logger.getLogger(SessionUtils.class);

	//获取用户bus_user   SESSION的值
	public static BusUser getLoginUser(HttpServletRequest request) {
		try {
			Object obj = request.getSession().getAttribute(ConfigConstant.SESSION_BUSINESS_KEY);
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



	//存入用户bus_user的值
	public static void setLoginUser(HttpServletRequest request, BusUser busUser) {
		try {
			request.getSession().setAttribute(ConfigConstant.SESSION_BUSINESS_KEY, JSON.toJSONString(busUser));
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 获取佣金平台管理员
	 * @param request
	 * @return
	 */
	public static UnionVerifier getVerifier(HttpServletRequest request) {
		try {
			Object obj = request.getSession().getAttribute(ConfigConstant.VERIFIER);
			if(obj != null){
				return JSON.parseObject(obj.toString(),UnionVerifier.class);
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
	 * 存入佣金平台管理员
	 * @param request
	 * @param unionVerifier
	 */
	public static void setUnionVerifier(HttpServletRequest request, UnionVerifier unionVerifier) {
		try {
			if(CommonUtil.isNotEmpty(unionVerifier)){
				request.getSession().setAttribute(ConfigConstant.VERIFIER, JSON.toJSONString(unionVerifier));
			}
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
			if (CommonUtil.isNotEmpty(member)) {
				request.getSession().setAttribute(
						ConfigConstant.SESSION_MEMBER, JSONObject.toJSONString(member));
			}
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	};


	/**
	 * 获取session中的商家会员信息
	 *
	 * @param request
	 * @return
	 */
	public static Member getLoginMember(HttpServletRequest request) {
		try {
			Object obj = request.getSession().getAttribute(ConfigConstant.SESSION_MEMBER);
			if(obj != null){
				return JSON.parseObject(obj.toString(), Member.class );
			}else{
				return null;
			}

		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();

		}
		return null;
	};
}
