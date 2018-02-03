package com.gt.union.refund.order.service;

import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.refund.order.entity.UnionRefundOrder;

/**
 * 服务接口
 *
 * @author hongjiye
 * @version 2018-02-02 16:58:00
 */
public interface IUnionRefundOrderService {

	/**
	 * 保存退款信息
	 * @param refundOrder
	 * @throws Exception
	 */
	void save(UnionRefundOrder refundOrder) throws Exception;

	/**
	 * 根据退款订单id查询退款订单
	 * @param refundOrderId		退款订单id
	 * @return
	 */
	UnionRefundOrder getValidById(Integer refundOrderId) throws Exception;

	/**
	 * 更新退款订单
	 * @param refundOrder	退款订单
	 */
	void update(UnionRefundOrder refundOrder) throws Exception;
}