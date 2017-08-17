package com.gt.union.common.constant;

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
	 * 联盟收集资料信息参数
	 */
	public static final String UNION_INFO_DICT = "1144";

	/**
	 * 创建联盟的等级相应成员数、金额、名称
	 */
	public static final String UNION_CREATE_INFO = "1145";

	/**
	 * 联盟已过期
	 */
	public static final String UNION_OVERDUE_MSG = "联盟已失效";


	/**
	 * 主账号权限
	 */
	public static final String UNION_BUS_PARENT_MSG = "请使用主账号权限";
}
