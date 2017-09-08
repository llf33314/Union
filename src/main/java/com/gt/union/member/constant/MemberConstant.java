package com.gt.union.member.constant;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public interface MemberConstant {
    /**
     * 是否是盟主：否
     */
    public static final int IS_UNION_OWNER_NO = 0;

    /**
     * 是否是盟主：是
     */
    public static final int IS_UNION_OWNER_YES = 1;

    /**
     * 盟员状态：申请加入
     */
    public static final int STATUS_APPLY_IN = 1;

    /**
     * 盟员状态：已加入
     */
    public static final int STATUS_IN = 2;

    /**
     * 盟员状态：申请退盟
     */
    public static final int STATUS_APPLY_OUT = 3;

    /**
     * 盟员状态：退盟过渡期
     */
    public static final int STATUS_OUTING = 4;
}
