package com.gt.union.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.union.api.client.user.BusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.entity.common.BusUser;
import com.gt.union.entity.common.Member;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class SessionUtils {
	private static final org.apache.log4j.Logger log = Logger
			.getLogger(SessionUtils.class);

	@Autowired
	private BusUserService busUserService;

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
	 * 设置session中的商家member会员信息
	 *
	 * @param request
	 * @return
	 */
	public static void setLoginMember(HttpServletRequest request,Member member) {
		try {
			if (CommonUtil.isEmpty(member)||CommonUtil.isEmpty(member.getId())) {// 充值

			} else {
				Integer busId = member.getBusid();
				member.setPass(isPassByBusUserVer(busId));
			}
			request.getSession().setAttribute(
					CommonConstant.SESSION_MEMBER, JSONObject.toJSONString(member));
			System.out.println(request.getSession());
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
	};

	public static boolean isPassByBusUserVer(Integer busId) {
		ApplicationContext context = CommonUtil.getApplicationContext();
		boolean isPass = false;
		BusUserService busUserService = context.getBean(BusUserService.class);
		BusUser busUser = busUserService.getBusUserById(busId);
		try {
			Integer days = DateTimeKit.daysBetween(new Date(),
					busUser.getEndTime());
			if (CommonUtil.isEmpty(days) || days < 0) {// 会员所对的商家已经过期
				isPass = true;
			}
		} catch (Exception e) {
			isPass = true;
		}
		return isPass;
	}

	/**
	 * 获取session中的商家会员信息
	 *
	 * @param request
	 * @return
	 */
	public static Member getLoginMember(HttpServletRequest request) {
		try {
			Object obj = request.getSession().getAttribute(CommonConstant.SESSION_MEMBER);
			if(obj != null){
				Member mem = JSONObject.toJavaObject((JSONObject.parseObject(obj.toString())),Member.class );
				return mem;
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
