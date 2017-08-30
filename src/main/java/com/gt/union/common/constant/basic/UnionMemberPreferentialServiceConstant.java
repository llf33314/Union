package com.gt.union.common.constant.basic;

/**
 * Created by Administrator on 2017/7/31 0031.
 */
public interface UnionMemberPreferentialServiceConstant {

    /**
     * 删除状态：否
     */
    public static final int DEL_STATUS_NO = 0;

    /**
     * 删除状态：是
     */
    public static final int DEL_STATUS_YES = 1;

    /**
     * 审核状态：未提交（注意：后台查询数据库时逻辑为优惠服务项为空）
     */
    public static final int VERIFY_STATUS_UNCOMMIT = 0;

    /**
     * 审核状态：未审核
     */
    public static final int VERIFY_STATUS_UNCHECK = 1;

    /**
     * 审核状态：审核通过
     */
    public static final int VERIFY_STATUS_PASS = 2;

    /**
     * 审核状态：审核不通过
     */
    public static final int VERIFY_STATUS_FAIL = 3;

    /**
     * 优惠项目最大字数限制
     */
    int SERVICE_NAME_MAX_LENGTH = 10;
}
