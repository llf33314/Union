package com.gt.union.common.constant.business;

/**
 * Created by Administrator on 2017/8/2 0002.
 */
public interface UnionBusinessRecommendConstant {

    /**
     * 删除状态：否
     */
    public static final int DEL_STATUS_NO = 0;

    /**
     * 删除状态：是
     */
    public static final int DEL_STATUS_YES = 1;

    /**
     * 是否受理：未处理
     */
    public static final int IS_ACCEPTANCE_UNCHECK = 0;

    /**
     * 是否受理：受理
     */
    public static final int IS_ACCEPTANCE_ACCEPT = 1;

    /**
     * 是否受理：拒绝
     */
    public static final int IS_ACCEPTANCE_REFUSE = 2;

    /**
     * 是否给予佣金：未处理
     */
    public static final int IS_CONFIRM_UNCHECK = 0;

    /**
     * 是否给予佣金：给予
     */
    public static final int IS_CONFIRM_CONFIRM = 1;

    /**
     * 是否给予佣金：拒绝
     */
    public static final int IS_CONFIRM_REFUSE = 2;

    /**
     * 推荐类型：线上
     */
    public static final int RECOMMEND_TYPE_ONLINE = 1;

    /**
     * 推荐类型：线下
     */
    public static final int RECOMMEND_TYPE_OFFLINE = 2;
}
