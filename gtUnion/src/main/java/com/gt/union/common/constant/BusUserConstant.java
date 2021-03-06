package com.gt.union.common.constant;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
public interface BusUserConstant {
    /**
     * 会员级别：普通，意味着没有公众号也能玩一些功能
     */
    public static final int LEVEL_COMMON = -1;
    /**
     * 会员级别：试用
     */
    public static final int LEVEL_TRIAL = 0;

    /**
     * 会员级别：基础
     */
    public static final int LEVEL_BASIC = 1;

    /**
     * 会员级别：升级
     */
    public static final int LEVEL_UPGRADE = 2;

    /**
     * 会员级别：高级
     */
    public static final int LEVEL_ADVANCED = 3;

    /**
     * 会员级别：至尊
     */
    public static final int LEVEL_EXTREME = 4;

    /**
     * 会员级别：白金
     */
    public static final int LEVEL_PLATINA = 5;

    /**
     * 会员级别：钻石
     */
    public static final int LEVEL_DIAMOND = 6;

    /**
     * 帐号类型：默认无效
     */
    public static final int ACCOUNT_TYPE_UNVALID = 0;

    /**
     * 帐号类型：主帐号
     */
    public static final int ACCOUNT_TYPE_MASTER = 1;

    /**
     * 帐号类型：子帐号
     */
    public static final int ACCOUNT_TYPE_SUB = 2;


    /**
     * 账号被冻结
     */
    public static final String UNION_USER_FREEZE_MSG = "该用户已被冻结";

    /**
     * 商家帐号已过期
     */
    public static final String UNION_BUS_OVERDUE_MSG = "商家帐号已过期";
}
