package com.gt.union.opportunity.brokerage.constant;

/**
 * 商机佣金常量接口
 *
 * @author linweicong
 * @version 2017-12-08 14:26:43
 */
public interface BrokerageConstant {
    /**
     * 佣金支付状态：未支付
     */
    int PAY_STATUS_PAYING = 1;

    /**
     * 佣金支付状态：支付成功
     */
    int PAY_STATUS_SUCCESS = 2;

    /**
     * 佣金支付状态：已退款
     */
    int PAY_STATUS_FAIL = 3;

    /**
     * 佣金支付类型：微信
     */
    int PAY_TYPE_WX = 1;

    /**
     * 佣金支付类型：支付宝
     */
    int PAY_TYPE_ALIPAY = 2;

    /**
     * 佣金收入类型：售卡
     */
    int INCOME_TYPE_CARD = 1;

    /**
     * 佣金收入类型：商机
     */
    int INCOME_TYPE_OPPORTUNITY = 2;

}
