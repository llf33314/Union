package com.gt.union.union.main.constant;

/**
 * 联盟常量接口
 *
 * @author linweicong
 * @version 2017-12-04 09:56:53
 */
public interface UnionConstant {
    /**
     * 是否开启积分：是
     */
    int IS_INTEGRAL_YES = 1;

    /**
     * 是否开启积分：否
     */
    int IS_INTEGRAL_NO = 0;

    /**
     * 加盟方式：推荐
     */
    int JOIN_TYPE_RECOMMEND = 1;

    /**
     * 加盟方式：申请、推荐
     */
    int JOIN_TYPE_APPLY_RECOMMEND = 2;

    /**
     * 盟主转移申请确认状态：确认中
     */
    int TRANSFER_CONFIRM_STATUS_PROCESS = 1;

    /**
     * 盟主转移申请确认状态：已接受
     */
    int TRANSFER_CONFIRM_STATUS_ACCEPT = 2;

    /**
     * 盟主转移申请确认状态：已拒绝
     */
    int TRANSFER_CONFIRM_STATUS_REJECT = 3;

    /**
     * 盟主服务订单状态：未支付
     */
    int PERMIT_ORDER_STATUS_PAYING = 1;

    /**
     * 盟主服务订单状态：支付成功
     */
    int PERMIT_ORDER_STATUS_SUCCESS = 2;

    /**
     * 盟主服务订单状态：支付失败
     */
    int PERMIT_ORDER_STATUS_FAIL = 3;

    /**
     * 盟主服务订单支付类型：微信
     */
    int PERMIT_PAY_TYPE_WX = 1;

    /**
     * 盟主服务订单支付类型：支付宝
     */
    int PERMIT_PAY_TYPE_ALIPAY = 2;

    /**
     * 入盟申请必填字段：负责人名称
     */
    String ITEM_KEY_DIRECTOR_NAME = "directorName";

    /**
     * 入盟申请必填字段：负责人联系电话
     */
    String ITEM_KEY_DIRECTOR_PHONE = "directorPhone";

    /**
     * 入盟申请必填字段：负责人邮箱
     */
    String ITEM_KEY_DIRECTOR_EMAIL = "directorEmail";

    /**
     * 入盟申请必填字段：理由
     */
    String ITEM_KEY_REASON = "reason";

}
