package com.gt.union.common.constant.basic;

/**
 * Created by Administrator on 2017/8/18 0018.
 */
public interface UnionTransferRecordConstant {
    /**
     * 删除状态：未删除
     */
    public static final int DEL_STATUS_NO = 0;

    /**
     * 删除状态：已删除
     */
    public static final int DEL_STATUS_YES = 1;

    /**
     * 审核状态：未审核
     */
    public static final int CONFIRM_STATUS_UNCHECK = 0;

    /**
     * 审核状态：确认
     */
    public static final int CONFIRM_STATUS_OK = 1;

    /**
     * 审核状态：拒绝
     */
    public static final int CONFIRM_STATUS_REFUSE = 2;

    /**
     * 不再提示：提示
     */
    public static final int NO_ADVICE_NO = 0;

    /**
     *
     */
    public static final int NO_ADVICE_YES = 1;
}
