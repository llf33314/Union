package com.gt.union.card.main.constant;

/**
 * 联盟卡常量接口
 *
 * @author linweicong
 * @version 2017-12-11 17:57:05
 */
public interface CardConstant {
    /**
     * 联盟卡类型：折扣卡
     */
    int TYPE_DISCOUNT = 1;

    /**
     * 联盟卡类型：活动卡
     */
    int TYPE_ACTIVITY = 2;

    /**
     * 支付状态：支付中或未支付
     */
    int PAY_STATUS_PAYING = 1;

    /**
     * 支付状态：支付成功
     */
    int PAY_STATUS_SUCCESS = 2;

    /**
     * 支付状态：支付失败
     */
    int PAY_STATUS_FAIL = 3;

    /**
     * 支付状态：已退款
     */
    int PAY_STATUS_RETURN = 4;

    /**
     * 支付类型：微信
     */
    int PAY_TYPE_WX = 1;

    /**
     * 支付类型：支付宝
     */
    int PAY_TYPE_ALIPAY = 2;

    /**
     * 统计类型：按天统计
     */
    int STATISTICS_TYPE_DAY = 1;

    /**
     * 统计类型：按月统计
     */
    int STATISTICS_TYPE_MONTH = 2;
}
