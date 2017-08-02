package com.gt.union.common.constant.basic;

/**
 * Created by Administrator on 2017/7/25 0025.
 */
public interface UnionApplyConstant {
    /**
     * 审核状态：未审核
     */
    public static final int APPLY_STATUS_UNCHECKED = 0;

    /**
     * 审核状态：审核通过
     */
    public static final int APPLY_STATUS_PASS = 1;

    /**
     * 审核状态：审核不通过
     */
    public static final int APPLY_STATUS_FAIL = 2;

    /**
     * 删除状态：否
     */
    public static final int DEL_STATUS_NO = 0;

    /**
     * 删除状态：是
     */
    public static final int DEL_STATUS_YES = 1;

    /**
     * 商家确认状态：申请状态
     */
    public static final int BUS_CONFIRM_STATUS_APPLY = 0;

    /**
     * 商家确认状态：未确认
     */
    public static final int BUS_CONFIRM_STATUS_UNCHECK = 1;

    /**
     * 商家确认状态：确认通过
     */
    public static final int BUS_CONFIRM_STATUS_PASS = 2;

    /**
     * 商家确认状态：拒绝
     */
    public static final int BUS_CONFIRM_STATUS_FAIL = 3;

    /**
     * 入盟申请类型：自由申请
     */
    public static final int APPLY_TYPE_FREE = 1;

    /**
     * 入盟申请类型：推荐申请
     */
    public static final int APPLY_TYPE_RECOMMEND = 2;
}
