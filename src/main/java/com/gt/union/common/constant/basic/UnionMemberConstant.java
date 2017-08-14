package com.gt.union.common.constant.basic;

/**
 * Created by Administrator on 2017/7/27 0027.
 */
public interface UnionMemberConstant {
    /**
     * 删除状态：未删除
     */
    public static final int DEL_STATUS_NO = 0;

    /**
     * 删除状态：已删除
     */
    public static final int DEL_STATUS_YES = 1;

    /**
     * 商家退出状态：正常
     */
    public static final int OUT_STATUS_NORMAL = 0;

    /**
     * 商家退出状态：未处理
     */
    public static final int OUT_STATUS_UNCHECKED = 1;

    /**
     * 商家退出状态：过渡期
     */
    public static final int OUT_STATUS_PERIOD = 2;

    /**
     * 是否是盟主：否
     */
    public static final int IS_UNION_OWNER_NO = 0;

    /**
     * 是否是盟主：是
     */
    public static final int IS_UNION_OWNER_YES = 1;


}
