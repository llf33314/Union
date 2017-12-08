package com.gt.union.opportunity.main.constant;

/**
 * 商机常量接口
 *
 * @author linweicong
 * @version 2017-12-08 08:44:19
 */
public interface OpportunityConstant {
    /**
     * 商机受理状态：受理中
     */
    int OPPORTUNITY_ACCEPT_STATUS_CONFIRMING = 1;

    /**
     * 商机受理状态：已接受
     */
    int OPPORTUNITY_ACCEPT_STATUS_CONFIRMED = 2;

    /**
     * 商机受理状态：已拒绝
     */
    int OPPORTUNITY_ACCEPT_STATUS_REJECT = 3;
}
