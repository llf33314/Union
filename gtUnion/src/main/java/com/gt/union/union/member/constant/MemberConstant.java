package com.gt.union.union.member.constant;

/**
 * 盟员常量接口
 *
 * @author linweicong
 * @version 2017-12-04 08:50:53
 */
public interface MemberConstant {
    /**
     * 是否盟主：是
     */
    int IS_UNION_OWNER_YES = 1;

    /**
     * 是否盟主：否
     */
    int IS_UNION_OWNER_NO = 0;

    /**
     * 盟员状态：申请入盟
     */
    int STATUS_APPLY_IN = 1;

    /**
     * 盟员状态：已入盟
     */
    int STATUS_IN = 2;

    /**
     * 盟员状态：申请退盟
     */
    int STATUS_APPLY_OUT = 3;

    /**
     * 盟员状态：退盟过渡期
     */
    int STATUS_OUT_PERIOD = 4;

    /**
     * 退盟类型：自己申请
     */
    int OUT_TYPE_APPLY = 1;

    /**
     * 退盟类型：盟主移出
     */
    int OUT_TYPE_REMOVE = 2;

    /**
     * 入盟类型：申请
     */
    int JOIN_TYPE_APPLY = 1;

    /**
     * 入盟类型：推荐
     */
    int JOIN_TYPE_RECOMMEND = 2;
}
