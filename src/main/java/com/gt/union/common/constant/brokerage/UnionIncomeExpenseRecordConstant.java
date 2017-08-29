package com.gt.union.common.constant.brokerage;

/**
 * Created by Administrator on 2017/8/28 0028.
 */
public interface UnionIncomeExpenseRecordConstant {
    /**
     * 删除状态：未删除
     */
    public static final int DEL_STATUS_NO = 0;

    /**
     * 删除状态：已删除
     */
    public static final int DEL_STATUS_YES = 1;

    /**
     * 收支类型：收入
     */
    public static final int TYPE_INCOME = 1;

    /**
     * 收支类型：支出
     */
    public static final int TYPE_EXPENSE = 2;

    /**
     * 收支来源：商机推荐
     */
    public static final int SOURCE_BUSINESS_RECOMMEND = 1;

    /**
     * 收支来源：售卡分成
     */
    public static final int SOURCE_CARD_DIVIDE = 2;

    /**
     * 收支来源：提现
     */
    public static final int SOURCE_WITHDRAWALS = 3;
}
