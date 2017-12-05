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

}
