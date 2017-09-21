package com.gt.union.member.constant;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public interface MemberConstant {
    /**
     * 是否是盟主：否
     */
    int IS_UNION_OWNER_NO = 0;

    /**
     * 是否是盟主：是
     */
    int IS_UNION_OWNER_YES = 1;

    /**
     * 盟员状态：申请加入
     */
    int STATUS_APPLY_IN = 1;

    /**
     * 盟员状态：已加入
     */
    int STATUS_IN = 2;

    /**
     * 盟员状态：申请退盟
     */
    int STATUS_APPLY_OUT = 3;

    /**
     * 盟员状态：退盟过渡期
     */
    int STATUS_OUTING = 4;

    /**
     * 加盟类型：申请
     */
    int JOIN_TYPE_JOIN = 1;

    /**
     * 加盟类型：推荐
     */
    int JOIN_TYPE_RECOMMEND = 2;

    /**
     * 退盟类型：自己申请退盟
     */
    int OUT_TYPE_APPLY = 1;

    /**
     * 退盟类型：盟主移出退盟
     */
    int OUT_TYPE_REMOVE = 2;
}
