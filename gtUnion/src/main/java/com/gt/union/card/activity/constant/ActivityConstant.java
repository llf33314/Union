package com.gt.union.card.activity.constant;

/**
 * 活动常量接口
 *
 * @author linweicong
 * @version 2017-12-11 17:55:27
 */
public interface ActivityConstant {
    /**
     * 活动卡状态：报名未开始
     */
    int STATUS_BEFORE_APPLY = 1;

    /**
     * 活动卡状态：报名中
     */
    int STATUS_APPLYING = 2;

    /**
     * 活动卡状态：报名已结束但售卡开始前
     */
    int STATUS_BEFORE_SELL = 3;

    /**
     * 活动卡状态：售卡中
     */
    int STATUS_SELLING = 4;

    /**
     * 活动卡状态：已结束
     */
    int STATUS_END = 5;

    /**
     * 联盟卡活动是否需要审核：是
     */
    int IS_PROJECT_CHECK_YES = 1;

    /**
     * 联盟卡活动是否需要审核：否
     */
    int IS_PROJECT_CHECK_NO = 0;
}
