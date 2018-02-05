package com.gt.union.refund.order.constant;

/**
 * @author hongjiye
 * @time 2018-02-03 9:20
 **/
public interface RefundOrderConstant {

    /**
     * 商机类型
     */
    int TYPE_OPPORTUNITY = 1;

    /**
     * 联盟卡类型
     */
    int TYPE_CARD = 2;

    /**
     * 退款申请中
     */
    int REFUND_STATUS_APPLYING = 1;

    /**
     * 退款成功
     */
    int REFUND_STATUS_SUCCESS = 2;

    /**
     * 退款失败
     */
    int REFUND_STATUS_FAIL = 3;
}
