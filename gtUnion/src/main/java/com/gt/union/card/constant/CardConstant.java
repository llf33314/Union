package com.gt.union.card.constant;

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
}
