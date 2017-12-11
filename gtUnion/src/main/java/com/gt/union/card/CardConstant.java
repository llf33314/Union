package com.gt.union.card;

/**
 * 联盟卡常量接口
 *
 * @author linweicong
 * @version 2017-12-04 15:30:32
 */
public interface CardConstant {
    /**
     * 联盟卡类型：折扣卡
     */
    int CARD_TYPE_DISCOUNT = 1;

    /**
     * 联盟卡类型：活动卡
     */
    int CARD_TYPE_ACTIVITY = 2;

    /**
     * 折扣卡默认名称
     */
    String DISCOUNT_CARD_NAME = "联盟折扣卡";

    /**
     * 活动卡状态：报名未开始
     */
    int ACTIVITY_STATUS_BEFORE_APPLY = 1;

    /**
     * 活动卡状态：报名中
     */
    int ACTIVITY_STATUS_APPLYING = 2;

    /**
     * 活动卡状态：报名已结束但售卡开始前
     */
    int ACTIVITY_STATUS_BEFORE_SELL = 3;

    /**
     * 活动卡状态：售卡中
     */
    int ACTIVITY_STATUS_SELLING = 4;

    /**
     * 活动卡状态：已结束
     */
    int ACTIVITY_STATUS_END = 5;

    /**
     * 联盟卡活动是否需要审核：是
     */
    int ACTIVITY_IS_PROJECT_CHECK_YES = 1;

    /**
     * 联盟卡活动是否需要审核：否
     */
    int ACTIVITY_IS_PROJECT_CHECK_NO = 0;

    /**
     * 项目状态：未提交
     */
    int PROJECT_STATUS_NOT_COMMIT = 1;

    /**
     * 项目状态：审核中
     */
    int PROJECT_STATUS_COMMITTED = 2;

    /**
     * 项目状态：已通过
     */
    int PROJECT_STATUS_ACCEPT = 3;

    /**
     * 项目状态：不通过
     */
    int PROJECT_STATUS_REJECT = 4;

    /**
     * 项目优惠类型：非ERP文本优惠
     */
    int ITEM_TYPE_TEXT = 1;

    /**
     * 项目优惠类型：ERP文本优惠
     */
    int ITEM_TYPE_ERP_TEXT = 2;

    /**
     * 项目优惠类型：ERP商品优惠
     */
    int ITEM_TYPE_ERP_GOODS = 3;

    /**
     * 项目优惠ERP类型：车小算
     */
    int ITEM_ERP_TYPE_CARD = 1;

    /**
     * 项目优惠ERP类型：样子
     */
    int ITEM_ERP_TYPE_LOOK = 2;

    /**
     * 消费类型：线上
     */
    int CONSUME_TYPE_ONLINE = 1;

    /**
     * 消费类型：线下
     */
    int CONSUME_TYPE_OFFLINE = 2;

    /**
     * 消费核销支付方式：现金
     */
    int CONSUME_PAY_TYPE_CASH = 0;

    /**
     * 消费核销支付方式：微信
     */
    int CONSUME_PAY_TYPE_WX = 1;

    /**
     * 消费核销支付方式：支付宝
     */
    int CONSUME_PAY_TYPE_ALIPAY = 2;

    /**
     * 消费核销支付状态：未支付
     */
    int CONSUME_PAY_STATUS_PAYING = 1;

    /**
     * 消费核销支付状态：支付成功
     */
    int CONSUME_PAY_STATUS_SUCCESS = 2;

    /**
     * 消费核销支付状态：已退款
     */
    int CONSUME_PAY_STATUS_FAIL = 3;

    /**
     * 消费核销表单支付方式：现金
     */
    int CONSUME_VO_PAY_TYPE_CASH = 1;

    /**
     * 消费核销表单支付方式：扫码支付
     */
    int CONSUME_VO_PAY_TYPE_CODE = 2;

    /**
     * 消费核销行业类型：线下
     */
    int CONSUME_BUSINESS_TYPE_OFFLINE = 0;

}
