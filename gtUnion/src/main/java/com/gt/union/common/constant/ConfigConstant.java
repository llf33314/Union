package com.gt.union.common.constant;

import com.gt.union.common.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;

/**
 * 系统配置全局常量
 * @author Mr.Yu
 *
 */
public interface ConfigConstant {

	/*****佣金平台管理员session key****/
	String VERIFIER = "verifier";
	/**
	 * 最大加入联盟数
	 */
	int MAX_UNION_APPLY = 3;

	/**
	 * 最大优惠项目数
	 */
	int MAX_PREFERENIAL_COUNT = 5;

	/**
	 * 短信model
	 */
	int SMS_UNION_MODEL = 33;

	/**
	 * 联盟支付model
	 */
	int PAY_MODEL = 36;

	/**
	 * 联盟提现model
	 */
	int ENTERPRISE_PAY_MODEL = 13;

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
	String CREATE_UNION_PAY_ORDER_CODE = "000060000";


	/**
	 * 扫码支付订单状态 001:等待用户扫码
	 */
	String USER_ORDER_STATUS_001="001";

	/**
	 * 扫码支付订单状态 002:确认扫码
	 */
	String USER_ORDER_STATUS_002="002";


	/**
	 * 扫码支付订单状态 003:完成订单
	 */
	String USER_ORDER_STATUS_003="003";


	/**
	 * 扫码支付订单状态 004:订单超时
	 */
	String USER_ORDER_STATUS_004="004";


	/**
	 * 扫码支付订单状态 005:订单支付失败
	 */
	String USER_ORDER_STATUS_005="005";

    /**
     * 开发者联系电话
     */
	String DEVELOPER_PHONE = "15986670850";
//	String DEVELOPER_PHONE = "13923639694";

	/**
	 * wxmp项目的密钥
	 */
	String WXMP_SIGN_KEY = PropertiesUtil.getWxmpSignKey();

    /**
     * wxmp公司名称
     */
	String WXMP_COMPANY = PropertiesUtil.getWxmpCompany();

	/**
	 * wxmp根路径
	 */
	String WXMP_ROOT_URL = PropertiesUtil.getWxmpUrl();

	/**
	 * wxmp请求密钥
	 */
	String WXMP_SIGNKEY = PropertiesUtil.getWxmpSignKey();

	/**
	 * wxmp多粉商家id  正式：2274
	 */
	Integer WXMP_DUOFEN_BUSID = PropertiesUtil.getDuofenBusId();

	/**
	 * 联盟根路径
	 */
	String UNION_ROOT_URL = PropertiesUtil.getUnionUrl();

	/**
	 * 联盟接口秘钥
	 */
	String UNION_SIGNKEY = PropertiesUtil.getUnionSignKey();

	/**
	 * 联盟卡手机端路径
	 */
	String UNION_PHONE_CARD_ROOT_URL = UNION_ROOT_URL + "/cardPhone/#/";

	/**
	 * 佣金平台手机端路径
	 */
	String UNION_PHONE_BROKERAGE_ROOT_URL = UNION_ROOT_URL + "/brokeragePhone/#/";

	/**
	 * 联盟加密秘钥
	 */
	String UNION_ENCRYPTKEY = PropertiesUtil.getEncryptKey();

	/**
	 * redis key前缀
	 */
	String UNION_REDIS_NAME_PREFIX = PropertiesUtil.redisNamePrefix();

	/**
	 * 会员请求密钥
	 */
	String MEMBER_SIGNKEY = PropertiesUtil.getMemberSignKey();

	/**
	 * 会员请求路径
	 */
	String MEMBER_ROOT_URL = PropertiesUtil.getMemberUrl();

	/**
	 * socket url
	 */
	String SOCKET_URL = PropertiesUtil.getSocketUrl();

	/**
	 * socket key
	 */
	String SOCKET_KEY = PropertiesUtil.getSocketKey();

	/**
	 * profiles
	 */
	String PROFILES = PropertiesUtil.getProfiles();

	/**
	 * 公众号二维码链接model
	 */
	int WXPUBLIC_QRCODE_MODEL = 10;

}

