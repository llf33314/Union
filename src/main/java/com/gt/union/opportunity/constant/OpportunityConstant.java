package com.gt.union.opportunity.constant;

/**
 * Created by Administrator on 2017/9/11 0011.
 */
public interface OpportunityConstant {

    /**
     * 受理中
     */
    int ACCEPT_NON = 1;

    /**
     * 已接受
     */
    int ACCEPT_YES = 2;

    /**
     * 已拒绝
     */
    int ACCEPT_NO = 3;

    /**
     * 线上
     */
    int TYPE_ONLINE = 1;

    /**
     * 线下
     */
    int TYPE_OFFLINE = 2;

    /**
     * 佣金支付订单前缀
     */
    String ORDER_PREFIX = "LMYJ";

    /**
     * 佣金已催促
     */
    int URGE_YES = 1;

    /**
     * 佣金未催促
     */
    int URGE_NO = 0;

    /**
     * 是否已支付：是
     */
    int IS_PAID_YES = 1;

    /**
     * 是否已支付：否
     */
    int IS_PAID_NO = 0;

    /**
     * 缓存键：商机id
     */
    int REDIS_KEY_OPPORTUNITY_ID = 1;

    /**
     * 缓存键：商机推荐者的盟员身份id
     */
    int REDIS_KEY_OPPORTUNITY_FROM_MEMBER_ID = 2;

    /**
     * 缓存键：商机被推荐者的盟员身份id
     */
    int REDIS_KEY_OPPORTUNITY_TO_MEMBER_ID = 3;

    /**
     * 缓存键：商机佣金id
     */
    int REDIS_KEY_RATIO_ID = 1;

    /**
     * 缓存键：商机佣金设置者的盟员身份id
     */
    int REDIS_KEY_RATIO_FROM_MEMBER_ID = 2;

    /**
     * 缓存键：商机拥挤被设置者的盟员身份id
     */
    int REDIS_KEY_RATIO_TO_MEMBER_ID = 3;
}
