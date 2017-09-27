package com.gt.union.preferential.constant;

/**
 * Created by Administrator on 2017/9/14 0014.
 */
public interface PreferentialConstant {
    /**
     * 审核状态：未提交
     */
    int STATUS_UNCOMMITTED = 1;

    /**
     * 审核状态：审核中
     */
    int STATUS_VERIFYING = 2;

    /**
     * 审核状态：审核通过
     */
    int STATUS_PASS = 3;

    /**
     * 审核状态：审核不通过
     */
    int STATUS_FAIL = 4;
}
