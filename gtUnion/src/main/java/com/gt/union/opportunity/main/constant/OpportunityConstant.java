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
    int ACCEPT_STATUS_CONFIRMING = 1;

    /**
     * 商机受理状态：已接受
     */
    int ACCEPT_STATUS_CONFIRMED = 2;

    /**
     * 商机受理状态：已拒绝
     */
    int ACCEPT_STATUS_REJECT = 3;

    /**
     * 是否已结算：否
     */
    int IS_CLOSE_NO = 0;

    /**
     * 是否已结算：是
     */
    int IS_CLOSE_YES = 1;

    /**
     * 推荐类型：线下
     */
    int TYPE_OFFLINE = 1;

    /**
     * 推荐类型：线上
     */
    int TYPE_ONLINE = 2;
}
