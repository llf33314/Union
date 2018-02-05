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
     * 最大消费金额 50000
     */
    double CONSUME_MAX_MONEY = 50000;

    /**
     * 活动卡最大发行数量 10000
     */
    int CARD_MAX_AMOUNT = 10000;

    /**
     * 活动卡最大售卡金额 5000
     */
    double CARD_MAX_MONEY = 5000;

    /**
     * 活动卡最大有效天数 730
     */
    int CARD_MAX_DAY = 730;

    /**
     * 佣金最大受理金额 50000
     */
    double BROKERAGE_MAX_MONEY = 50000;

    /**
     * 优惠项目和商品最大数量 10000
     */
    int PROJECT_ITEM_MAX_COUNT = 10000;

    /**
     * 开发者联系电话
     */
    String DEVELOPER_PHONE = "15986670850";
//	String DEVELOPER_PHONE = "13923639694";

    /**
     * 2：车小算
     * 联盟erp项目类型
     */
    int[] UNION_USER_ERP_TYPE = {2};

    /**
     * 支付模块：联盟许可
     */
    String PAY_MODEL_PERMIT = "XK";

    /**
     * 支付模块：商机佣金
     */
    String PAY_MODEL_OPPORTUNITY = "SJ";

    /**
     * 支付模块：消费核销
     */
    String PAY_MODEL_CONSUME = "XF";

    /**
     * 支付模块：联盟卡
     */
    String PAY_MODEL_CARD = "CA";

    /**
     * 支付模块：佣金提现
     */
    String PAY_MODEL_WITHDRAWAL = "TX";

    /**
     * 是否启用模拟数据(0：否 1：是)
     */
    int IS_MOCK = 0;

    /**
     * 是否启用开发调试账号(0：否 1：是)
     */
    int IS_DEV = 0;

    /**
     * 联盟卡手机端根路径
     */
    String CARD_PHONE_BASE_URL = PropertiesUtil.getUnionUrl() + "/cardPhone/#/";

    /**
     * 支付终端 1：pc 2：h5 3：小程序
     */
    Integer[] appType = {1, 2, 3};
}

