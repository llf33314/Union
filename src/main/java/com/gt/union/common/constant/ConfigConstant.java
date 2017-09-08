package com.gt.union.common.constant;

import com.gt.union.common.util.PropertiesUtil;

/**
 * 系统配置全局常量
 * @author Mr.Yu
 *
 */
public class ConfigConstant {
	/** 商家账户在session key*/
	public static String SESSION_BUSINESS_KEY = "business_key" ;

	/*****粉丝session key****/
	public static String SESSION_MEMBER = "member";

	/*****佣金平台管理员session key****/
	public static String VERIFIER = "verifier";
	/**
	 * 最大加入联盟数
	 */
	public static int MAX_UNION_APPLY = 3;

	/**
	 * 最大优惠项目数
	 */
	public static int MAX_PREFERENIAL_COUNT = 5;

	/**
	 * 短信model
	 */
	public static int SMS_UNION_MODEL = 33;

	/**
	 * 联盟支付model
	 */
	public static int PAY_MODEL = 36;

	/**
	 * 联盟提现model
	 */
	public static int ENTERPRISE_PAY_MODEL = 13;

	/**
	 * 省级
	 */
	public static String PROVIENCE_LEVEL = "2";

	/**
	 * 市级
	 */
	public static String CITY_LEVEL = "3";

	/**
	 * 区级
	 */
	public static String DISTRICT_LEVEL = "4";

	/**
	 * 创建联盟支付内部订单前缀
	 */
	public static final String CREATE_UNION_PAY_ORDER_CODE = "000060000";


	/**
	 * 扫码支付订单状态 001:等待用户扫码
	 */
	public final static String USER_ORDER_STATUS_001="001";

	/**
	 * 扫码支付订单状态 002:确认扫码
	 */
	public final static String USER_ORDER_STATUS_002="002";


	/**
	 * 扫码支付订单状态 003:完成订单
	 */
	public final static String USER_ORDER_STATUS_003="003";


	/**
	 * 扫码支付订单状态 004:订单超时
	 */
	public final static String USER_ORDER_STATUS_004="004";


	/**
	 * 扫码支付订单状态 005:订单支付失败
	 */
	public final static String USER_ORDER_STATUS_005="005";

    /**
     * 开发者联系电话
     */
	public final static String DEVELOPER_PHONE = "15986670850";
//	public final static String DEVELOPER_PHONE = "13923639694";

	/**
	 * wxmp项目的密钥
	 */
	public static final String WXMP_SIGN_KEY = PropertiesUtil.getWxmpSignKey();

    /**
     * wxmp公司名称
     */
	public static final String WXMP_COMPANY = PropertiesUtil.getWxmpCompany();
}
