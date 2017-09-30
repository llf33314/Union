package com.gt.union.main.constant;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public interface MainConstant {
    /**
     * 是否开启积分：否
     */
    int IS_INTEGRAL_NO = 0;

    /**
     * 是否开启积分：是
     */
    int IS_INTEGRAL_YES = 1;

    /**
     * 红黑卡类型：黑卡
     */
    int CHARGE_TYPE_BLACK = 1;

    /**
     * 红黑卡类型：红卡
     */
    int CHARGE_TYPE_RED = 2;

    /**
     * 联盟卡是否启用：否
     */
    int CHARGE_IS_AVAILABLE_NO = 0;

    /**
     * 联盟卡是否启用：是
     */
    int CHARGE_IS_AVAILABLE_YES = 1;

    /**
     * 联盟卡是否收费：否
     */
    int CHARGE_IS_CHARGE_NO = 0;

    /**
     * 联盟卡是否收费：是
     */
    int CHARGE_IS_CHARGE_YES = 1;

    /**
     * 旧会员是否收费：是
     */
    int CHARGE_OLD_IS_YES = 1;

    /**
     * 旧会员是否收费：否
     */
    int CHARGE_OLD_IS_NO = 0;

    /**
     * 公告长度
     */
    int NOTICE_MAX_LENGTH = 50;

    /**
     * 加盟方式：默认为申请
     */
    int MAIN_JOIN_TYPE_DEFAULT = 1;

    /**
     * 加盟方式：申请、推荐
     */
    int MAIN_JOIN_TYPE_BOTH = 2;

    /**
     * 转移记录的确认状态：确认中
     */
    int TRANSFER_CONFIRM_STATUS_HANDLING = 1;

    /**
     * 转移记录的确认状态：已确认
     */
    int TRANSFER_CONFIRM_STATUS_YES = 2;

    /**
     * 转移记录的确认状态：已拒绝
     */
    int TRANSFER_CONFIRM_STATUS_NO = 3;

    /**
     * 创建联盟支付：未支付
     */
    int PERMIT_ORDER_STATUS_UNPAY = 1;

    /**
     * 创建联盟支付：已支付
     */
    int PERMIT_ORDER_STATUS_SUCCESS = 2;

    /**
     * 创建联盟支付方式：微信
     */
    int PERMIT_PAY_TYPE_WX = 1;

    /**
     * 创建联盟支付方式：支付宝
     */
    int PERMIT_PAY_TYPE_ALI = 3;

    /**
     * 缓存键：字典id
     */
    int REDIS_KEY_DICT_ID = 1;

    /**
     * 缓存键：字典所属的联盟id
     */
    int REDIS_KEY_DICT_UNION_ID = 2;

    /**
     * 缓存键：升级收费id
     */
    int REDIS_KEY_CHARGE_ID = 1;

    /**
     * 缓存键：升级收费所关联的联盟id
     */
    int REDIS_KEY_CHARGE_UNION_ID = 2;

    /**
     * 缓存键：公告id
     */
    int REDIS_KEY_NOTICE_ID = 1;

    /**
     * 缓存键：公告所属的联盟id
     */
    int REDIS_KEY_NOTICE_UNION_ID = 2;
}
