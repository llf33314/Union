package com.gt.union.common.constant;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public interface CommonConstant {
    /**
     * 删除状态：未删除
     */
    public static final int DEL_STATUS_NO = 0;

    /**
     * 删除状态：已删除
     */
    public static final int DEL_STATUS_YES = 1;

    /**
     * 异常类型：参数异常
     */
    public static final String PARAM_ERROR = "参数错误";

    /**
     * 异常类型：操作异常
     */
    public static final String OPERATE_ERROR = "操作失败";

    /**
     * 异常类型：系统异常
     */
    public static final String SYS_ERROR = "系统异常";

    /**
     * 主账号权限
     */
    public static final String UNION_BUS_PARENT_MSG = "请使用主账号权限";

    /**
     * 商家帐号已过期
     */
    public static final String UNION_BUS_OVERDUE_MSG = "商家帐号已过期";

    /**
     * 盟员身份无效
     */
    public static final String UNION_MEMBER_INVALID = "不具有盟员身份或已过期";

    /**
     * 需要盟主身份
     */
    public static final String UNION_MEMBER_NEED_OWNER = "非盟主身份无法操作";

    /**
     * 通用是否：否
     */
    public static final int COMMON_NO = 0;

    /**
     * 通用是否：是
     */
    public static final int COMMON_YES = 1;

    /**
     * 联盟默认有效期
     */
    public static final String UNION_VALIDITY_DEFAULT = "2020-09-01 14:00:00";
}
