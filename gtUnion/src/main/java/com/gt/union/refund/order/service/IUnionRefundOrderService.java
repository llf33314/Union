package com.gt.union.refund.order.service;

import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.refund.order.entity.UnionRefundOrder;

import java.util.List;

/**
 * 服务接口
 *
 * @author hongjiye
 * @version 2018-02-02 16:58:00
 */
public interface IUnionRefundOrderService {

    /**
     * 保存退款信息
     *
     * @param refundOrder
     * @throws Exception
     */
    void save(UnionRefundOrder refundOrder) throws Exception;

    /**
     * 根据退款订单id查询退款订单
     *
     * @param refundOrderId 退款订单id
     * @return
     * @throws Exception
     */
    UnionRefundOrder getValidById(Integer refundOrderId) throws Exception;

    /**
     * 更新退款订单
     *
     * @param refundOrder 退款订单
     * @throws Exception
     */
    void update(UnionRefundOrder refundOrder) throws Exception;

    /**
     * 根据退款订单号和退款状态查退款信息
     *
     * @param orderNo 订单号
     * @param status  退款状态
     * @param type    退款类型
     * @return
     * @throws Exception
     */
    UnionRefundOrder getValidByOrderNoAndStatusAndType(String orderNo, Integer status, Integer type) throws Exception;

    /**
     * 根据退款订单号和退款状态查退款信息
     *
     * @param status 退款状态
     * @param type   退款类型
     * @return
     * @throws Exception
     */
    List<UnionRefundOrder> listValidByStatusAndType(Integer status, Integer type) throws Exception;
}
