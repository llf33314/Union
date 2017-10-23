package com.gt.union.brokerage.constant;

/**
 * Created by Administrator on 2017/9/11 0011.
 */
public interface BrokerageConstant {
    /**
     * 收支类型：收入
     */
    int TYPE_INCOME = 1;

    /**
     * 收支类型：支出
     */
    int TYPE_EXPENSE = 2;

    /**
     * 佣金支付状态：佣金未支付
     */
    int BROKERAGE_PAY_STATUS_NON = 1;

    /**
     * 佣金支付状态：佣金已支付
     */
    int BROKERAGE_PAY_STATUS_YES = 2;

    /**
     * 佣金支付状态：佣金已退款
     */
    int BROKERAGE_PAY_STATUS_REFUND = 3;

    /**
     * 佣金支付状态：微信支付
     */
    int BROKERAGE_PAY_TYPE_WX = 1;

    /**
     * 佣金支付状态：支付宝支付
     */
    int BROKERAGE_PAY_TYPE_ALIPAY = 2;

    /**
     * 来源类型：联盟卡
     */
    int SOURCE_TYPE_CARD = 1;

    /**
     * 来源类型：商机
     */
    int SOURCE_TYPE_OPPORTUNITY = 2;
    
}
