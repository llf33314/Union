package com.gt.union.common.constant;

import com.gt.union.common.util.PropertiesUtil;

/**
 * 系统配置全局常量类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
public interface ConfigConstant {

    /**
     * 最大加入联盟数
     */
    int MAX_UNION_APPLY = 3;

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
     * 公众号二维码链接model
     */
    int WXPUBLIC_QRCODE_MODEL = 10;

    /**
     * 创建联盟支付内部订单前缀
     */
    String CREATE_UNION_PAY_ORDER_CODE = "000060000";


    /**
     * 扫码支付订单状态 001:等待用户扫码
     */
    String USER_ORDER_STATUS_001 = "001";

    /**
     * 扫码支付订单状态 002:确认扫码
     */
    String USER_ORDER_STATUS_002 = "002";


    /**
     * 扫码支付订单状态 003:完成订单
     */
    String USER_ORDER_STATUS_003 = "003";


    /**
     * 扫码支付订单状态 004:订单超时
     */
    String USER_ORDER_STATUS_004 = "004";


    /**
     * 扫码支付订单状态 005:订单支付失败
     */
    String USER_ORDER_STATUS_005 = "005";

    /**
     * 开发者联系电话
     */
    String DEVELOPER_PHONE = "15986670850";
//	String DEVELOPER_PHONE = "13923639694";

    /**
     * 联盟根路径
     */
    String UNION_ROOT_URL = PropertiesUtil.getUnionUrl();

    /**
     * 联盟卡手机端路径
     */
    String UNION_PHONE_CARD_ROOT_URL = UNION_ROOT_URL + "/cardPhone/#/";

    /**
     * 佣金平台手机端路径
     */
    String UNION_PHONE_BROKERAGE_ROOT_URL = UNION_ROOT_URL + "/brokeragePhone/#/";

    /**
     * 联盟erp项目类型
     */
    String[] UNION_USER_ERP_TYPE = {"2"};
}

