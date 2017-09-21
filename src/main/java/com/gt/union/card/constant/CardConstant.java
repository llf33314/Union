package com.gt.union.card.constant;

/**
 * Created by Administrator on 2017/9/11 0011.
 */
public interface CardConstant {
    /**
     * 积分状态：收入
     */
    public static final int INTEGRAL_STATUS_IN = 1;

    /**
     * 积分状态：支出
     */
    public static final int INTEGRAL_STATUS_OUT = 2;

    /**
     * 消费
     */
    public static final int INTEGRAL_TYPE_CONSUME = 1;

    /**
     * 赠送
     */
    public static final int INTEGRAL_TYPE_GIVE = 2;

    /**
     * 联盟卡类型：黑卡
     */
    public static final int TYPE_BLACK = 1;

    /**
     * 联盟卡类型：红卡
     */
    public static final int TYPE_RED = 2;

    /**
     * 免费会员
     */
    int IS_OLD_YES = 1;

    /**
     * 付费会员
     */
    int IS_OLD_NO = 0;

    /**
     * 免费卡的有效期
     */
    String CARD_FREE_VALIDITY = "2030-09-01 14:00:00";
}
