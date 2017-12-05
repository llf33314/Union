package com.gt.union.common.constant;

/**
 * 短信验证码常量
 * @author hongjiye
 * @time 2017-12-5 14:52
 */
public interface SmsCodeConstant {

	/**
	 * 联盟卡手机登录验证码类型
	 */
	int UNION_CARD_LOGIN_TYPE = 1;

	/**
	 * 联盟卡手机登录验证码内容
	 */
	String UNION_CARD_LOGIN_MSG = "联盟卡登录验证码：";

	/**
	 * 办理联盟卡验证码类型
	 */
	int APPLY_UNION_CARD_TYPE = 2;

	/**
	 * 办理联盟卡验证码内容
	 */
	String APPLY_UNION_CARD_MSG = "办理联盟卡验证码：";

	/**
	 * 佣金平台登录验证码类型
	 */
	int BROKERAGE_LOGIN_TYPE = 3;

	/**
	 * 佣金平台登录验证码内容
	 */
	String BROKERAGE_LOGIN_MSG = "佣金平台登录验证码：";

	/**
	 * 联盟卡绑定验证码类型
	 */
	int UNION_CARD_PHONE_BIND_TYPE = 4;

	/**
	 * 联盟卡绑定验证码内容
	 */
	String UNION_CARD_PHONE_BIND_MSG = "联盟卡绑定验证码：";

	/**
	 * 添加平台管理员验证码类型
	 */
	int UNION_VERIFIER_TYPE = 5;

	/**
	 * 添加平台管理员验证码内容
	 */
	String UNION_VERIFIER_MSG = "佣金平台管理员验证码:";
}
