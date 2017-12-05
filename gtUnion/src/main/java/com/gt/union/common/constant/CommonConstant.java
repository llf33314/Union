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
    String UNION_BUS_PARENT_MSG = "请使用主账号权限";

    /**
     * 商家帐号已过期
     */
    String UNION_BUS_OVERDUE_MSG = "商家帐号已过期";

    /**
     * 找不到商家帐号
     */
    String UNION_BUS_NOT_FOUND = "找不到商家帐号";
    
    /**
     * 联盟已过期
     */
    String UNION_INVALID = "联盟已过期";

    /**
     * 不具有读权限，只有已加盟、申请退盟状态和退盟过渡期才有，未加盟、申请加盟和已退盟不具有
     */
    String UNION_READ_REJECT = "不具有联盟读权限";

    /**
     * 不具有写权限，只有已加盟和申请退盟状态才有，未加盟、申请加盟、退盟过渡期和已退盟不具有
     */
    String UNION_WRITE_REJECT = "不具有联盟写权限";

    /**
     * 需要盟主身份
     */
    String UNION_NEED_OWNER = "非盟主身份无法操作";

    /**
     * 联盟默认有效期
     */
    String UNION_VALIDITY_DEFAULT = "2030-09-01 14:00:00";

    /**
     * 验证码有误
     */
    String CODE_ERROR_MSG = "验证码有误";

}
