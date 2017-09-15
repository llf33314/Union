package com.gt.union.brokerage.constant;

/**
 * Created by Administrator on 2017/9/11 0011.
 */
public interface BrokerageConstant {
    /**
     * 收支类型：收入
     */
    public static final int TYPE_INCOME = 1;

    /**
     * 收支类型：支出
     */
    public static final int TYPE_EXPENSE = 2;

    /**
     * 佣金未支付
     */
    int BROKERAGE_PAY_STATUS_NON = 1;

    /**
     * 佣金已支付
     */
    int BROKERAGE_PAY_STATUS_YES = 2;

    /**
     * 佣金已退款
     */
    int BROKERAGE_PAY_STATUS_REFUND = 3;

    /**
     * 微信支付
     */
    int BROKERAGE_PAY_TYPE_WX = 1;

    /**
     * 支付宝支付
     */
    int BROKERAGE_PAY_TYPE_ALIPAY = 2;

}
