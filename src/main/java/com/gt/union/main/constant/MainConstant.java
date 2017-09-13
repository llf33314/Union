package com.gt.union.main.constant;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public interface MainConstant {
    /**
     * 是否开启积分：否
     */
    public static final int IS_INTEGRAL_NO = 0;

    /**
     * 是否开启积分：是
     */
    public static final int IS_INTEGRAL_YES = 1;

    /**
     * 红黑卡类型：黑卡
     */
    public static final int CHARGE_TYPE_BLACK = 1;

    /**
     * 红黑卡类型：红卡
     */
    public static final int CHARGE_TYPE_RED = 2;

    /**
     * 联盟卡是否启用：否
     */
    public static final int CHARGE_IS_AVAILABLE_NO = 0;

    /**
     * 联盟卡是否启用：是
     */
    public static final int CHARGE_IS_AVAILABLE_YES = 1;

    /**
     * 联盟卡是否收费：否
     */
    public static final int CHARGE_IS_CHARGE_NO = 0;

    /**
     * 联盟卡是否收费：是
     */
    public static final int CHARGE_IS_CHARGE_YES = 1;

    /**
     * 公告长度
     */
    public static final int NOTICE_MAX_LENGTH = 50;

    /**
     * 加盟方式：默认为申请
     */
    public static final int MAIN_JOIN_TYPE_DEFAULT = 1;

    /**
     * 加盟方式：申请、推荐
     */
    public static final int MAIN_JOIN_TYPE_BOTH = 2;

    /**
     * 转移记录的确认状态：确认中
     */
    public static final int TRANSFER_CONFIRM_STATUS_HANDLING = 1;

    /**
     * 转移记录的确认状态：已确认
     */
    public static final int TRANSFER_CONFIRM_STATUS_YES = 2;

    /**
     * 转移记录的确认状态：已拒绝
     */
    public static final int TRANSFER_CONFIRM_STATUS_NO = 3;
}
