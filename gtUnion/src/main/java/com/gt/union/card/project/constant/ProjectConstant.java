package com.gt.union.card.project.constant;

/**
 * 活动项目优惠常量接口
 *
 * @author linweicong
 * @version 2017-12-11 17:57:59
 */
public interface ProjectConstant {

    /**
     * 项目状态：未提交
     */
    int STATUS_NOT_COMMIT = 1;

    /**
     * 项目状态：审核中
     */
    int STATUS_COMMITTED = 2;

    /**
     * 项目状态：已通过
     */
    int STATUS_ACCEPT = 3;

    /**
     * 项目状态：不通过
     */
    int STATUS_REJECT = 4;

    /**
     * 项目优惠类型：非ERP文本优惠
     */
    int TYPE_TEXT = 1;

    /**
     * 项目优惠类型：ERP文本优惠
     */
    int TYPE_ERP_TEXT = 2;

    /**
     * 项目优惠类型：ERP商品优惠
     */
    int TYPE_ERP_GOODS = 3;

    /**
     * 项目优惠ERP类型：车小算
     */
    int ERP_TYPE_CARD = 1;

    /**
     * 项目优惠ERP类型：样子
     */
    int ERP_TYPE_LOOK = 2;
}
