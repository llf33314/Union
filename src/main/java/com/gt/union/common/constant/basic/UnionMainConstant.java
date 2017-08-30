package com.gt.union.common.constant.basic;

/**
 * Created by Administrator on 2017/8/2 0002.
 */
public interface UnionMainConstant {
    /**
     * 删除状态：未删除
     */
    public static final int DEL_STATUS_NO = 0;

    /**
     * 删除状态：已删除
     */
    public static final int DEL_STATUS_YES = 1;

    /**
     * 是否有效：是
     */
    public static final int VALID_OK = 0;

    /**
     * 初始化或新建联盟时，默认的盟员数为1，即盟主
     */
    public static final int MEMBER_NUM_INIT = 1;

    /**
     * 初始化或新建联盟时，默认的联盟级别为1
     */
    public static final int LEVEL_INIT = 1;

    /**
     * 加盟方式：只允许申请
     */
    public static final int JOIN_TYPE_APPLY_ONLY = 1;

    /**
     * 加盟方式：允许申请和推荐
     */
    public static final int JOIN_TYPE_APPLY_RECOMMEND = 2;

    /**
     * 开启红卡
     */
    int RED_CARD_OPEN = 1;

    /**
     * 未开启红卡
     */
    int RED_CARD_NON_OPEN = 0;

    /**
     * 开启黑卡收费
     */
    int BLACK_CARD_OPEN = 1;

    /**
     * 未开启黑卡收费
     */
    int BLACK_CARD_NON_OPEN = 0;

    /**
     * 老会员收费
     */
    int OLD_CARD_CHARGE = 1;

    /**
     * 老会员不收费
     */
    int OLD_CARD_NON_CHARGE = 0;

    /**
     * 开启积分
     */
    int IS_INTEGRAL_YES = 1;

    /**
     * 未开启积分
     */
    int IS_INTEGRAL_NO = 0;
}
