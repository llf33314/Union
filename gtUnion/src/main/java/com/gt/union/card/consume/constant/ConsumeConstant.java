package com.gt.union.card.consume.constant;

/**
 * 消费核销常量接口
 *
 * @author linweicong
 * @version 2017-12-11 17:59:54
 */
public interface ConsumeConstant {
    /**
     * 消费类型：线上
     */
    int TYPE_ONLINE = 1;

    /**
     * 消费类型：线下
     */
    int TYPE_OFFLINE = 2;

    /**
     * 消费核销支付方式：现金
     */
    int PAY_TYPE_CASH = 0;

    /**
     * 消费核销支付方式：微信
     */
    int PAY_TYPE_WX = 1;

    /**
     * 消费核销支付方式：支付宝
     */
    int PAY_TYPE_ALIPAY = 2;

    /**
     * 消费核销支付状态：未支付
     */
    int PAY_STATUS_PAYING = 1;

    /**
     * 消费核销支付状态：支付成功
     */
    int PAY_STATUS_SUCCESS = 2;

    /**
     * 消费核销支付状态：已退款
     */
    int PAY_STATUS_FAIL = 3;

    /**
     * 消费核销表单支付方式：现金
     */
    int VO_PAY_TYPE_CASH = 1;

    /**
     * 消费核销表单支付方式：扫码支付
     */
    int VO_PAY_TYPE_CODE = 2;

    /**
     * 消费核销行业类型：线下
     */
    int BUSINESS_TYPE_OFFLINE = 0;
}
