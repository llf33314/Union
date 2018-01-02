package com.gt.union.common.constant;

/**
 * 通用常量类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
public interface CommonConstant {
    /**
     * 删除状态：未删除
     */
    int DEL_STATUS_NO = 0;

    /**
     * 删除状态：已删除
     */
    int DEL_STATUS_YES = 1;

    /**
     * 通用是否：否
     */
    int COMMON_NO = 0;

    /**
     * 通用是否：是
     */
    int COMMON_YES = 1;

    /**
     * 异常类型：参数异常
     */
    String PARAM_ERROR = "参数错误";

    /**
     * 异常类型：操作异常
     */
    String OPERATE_ERROR = "操作失败";

    /**
     * 异常类型：系统异常
     */
    String SYS_ERROR = "系统异常";

    /**
     * 主账号权限
     */
    String BUS_PARENT_TIP = "请使用主账号权限";

    /**
     * 找不到商家帐号
     */
    String BUS_NOT_FOUND = "找不到商家帐号";

    /**
     * 联盟不存在或已过期
     */
    String UNION_INVALID = "联盟不存在或已过期";

    /**
     * 找不到联盟信息
     */
    String UNION_NOT_FOUND = "找不到联盟信息";

    /**
     * 非盟主身份无法操作
     */
    String UNION_OWNER_ERROR = "非盟主身份无法操作";

    /**
     * 非盟员身份无法操作
     */
    String UNION_MEMBER_ERROR = "非盟员身份无法操作";

    /**
     * 找不到盟员信息或已退盟
     */
    String MEMBER_NOT_FOUND = "找不到盟员信息";
    
    /**
     * 验证码有误
     */
    String CODE_ERROR_MSG = "验证码有误";

    /**
     * token有误
     */
    String TOKEN_ERROR = "无效的token";

    /**
     * token为空
     */
    String TOKEN_NULL = "token不能为空";

}
