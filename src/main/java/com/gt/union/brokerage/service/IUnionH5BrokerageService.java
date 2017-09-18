package com.gt.union.brokerage.service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
public interface IUnionH5BrokerageService {

	/**
	 * 佣金平台手机端验证登录
	 * @param type        登录类型 1：商家账号 2：手机
	 * @param username    用户名
	 * @param userpwd    用户密码
	 * @param phone        手机号
	 * @param code        验证码
	 * @param request
	 */
	void checkLogin(Integer type, String username, String userpwd, String phone, String code, HttpServletRequest request) throws Exception;
}
