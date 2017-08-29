package com.gt.union.common.constant;

import com.gt.union.common.util.PropertiesUtil;

/**
 * 系统全局常量
 * @author Mr.Yu
 *
 */
public class CommonConstant {
	/** 商家账户在session key*/
	public static String SESSION_BUSINESS_KEY = "business_key" ;

	/*****商家主账号id key****/
	public static String SESSION_BUSUSER_ID_KEY = "bususer_key";

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
	 * 创建联盟支付产品号前缀字典代码
	 */
	public final static String CREATE_UNION_PAY_PRODUCTID_CODE="00006100";

	/**
	 * 创建无付款联盟代码前缀
	 */
	public final static String CREATE_UNION_UN_PAY_PRODUCTID_CODE="00006200";

    /**
     * 开发者联系电话
     */
	public final static String DEVELOPER_PHONE = "15986670850";
//	public final static String DEVELOPER_PHONE = "13923639694";

	/**
	 * 联盟已过期
	 */
	public static final String UNION_OVERDUE_MSG = "联盟已失效";

    /**
     * 商家帐号已过期
     */
	public static final String UNION_BUS_OVERDUE_MSG = "商家帐号已过期";

	/**
	 * 主账号权限
	 */
	public static final String UNION_BUS_PARENT_MSG = "请使用主账号权限";

	/**
	 * 联盟成员数上限
	 */
	public static final String UNION_NUM_MAX_MSG = "联盟成员数已达上限";

	/**
	 * 加入的联盟数上限
	 */
	public static final String UNION_MEMBER_NUM_MAX_MSG = "您加入的联盟已达上限";

	/**
	 * 没有权限操作
	 */
	public static final String UNION_MEMBER_NON_AUTHORITY_MSG = "您没有权限";

	/**
	 * 没有盟主权限
	 */
	public static final String UNION_OWNER_NON_AUTHORITY_MSG = "您没有盟主权限";

	/**
	 * wxmp项目的密钥
	 */
	public static final String WXMP_SIGN_KEY = PropertiesUtil.getWxmpSignKey();

    /**
     * wxmp公司名称
     */
	public static final String WXMP_COMPANY = PropertiesUtil.getWxmpCompany();
}
