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
     * 查询信息类型：盟员列表
     */
    public static final int LIST_TYPE_MEMBER = 1;

    /**
     * 查询信息类型：退盟申请列表
     */
    public static final int LIST_TYPE_OUT = 2;

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
}
