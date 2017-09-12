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

    /**
     * 最大加入联盟数
     */
    public static int MAX_UNION_APPLY = 3;

    /**
     * 加盟类型：申请
     */
    public static int JOIN_TYPE_JOIN = 1;

    /**
     * 加盟类型：推荐
     */
    public static int JOIN_TYPE_RECOMMEND = 2;

    /**
     * 没有盟主权限
     */
    public static final String UNION_OWNER_NON_AUTHORITY_MSG = "您没有盟主权限";

    /**
     * 联盟成员数上限
     */
    public static final String UNION_NUM_MAX_MSG = "联盟成员数已达上限";

    /**
     * 加入的联盟数上限
     */
    public static final String UNION_MEMBER_NUM_MAX_MSG = "您加入的联盟已达上限";

    /**
     * 没有权限操作
     */
    public static final String UNION_MEMBER_NON_AUTHORITY_MSG = "您没有权限";
}
