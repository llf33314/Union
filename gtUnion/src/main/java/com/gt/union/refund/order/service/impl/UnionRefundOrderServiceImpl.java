package com.gt.union.refund.order.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.refund.order.dao.IUnionRefundOrderDao;
import com.gt.union.refund.order.entity.UnionRefundOrder;
import com.gt.union.refund.order.service.IUnionRefundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author hongjiye
 * @version 2018-02-02 16:58:00
 */
@Service
public class UnionRefundOrderServiceImpl implements IUnionRefundOrderService {
    @Autowired
    private IUnionRefundOrderDao unionRefundOrderDao;

	@Override
	public void save(UnionRefundOrder refundOrder) throws Exception {
		if (refundOrder == null) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}

		unionRefundOrderDao.insert(refundOrder);
	}

	@Override
	public UnionRefundOrder getValidById(Integer refundOrderId) throws Exception {
		if (refundOrderId == null) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		wrapper.eq("id", refundOrderId);
		return unionRefundOrderDao.selectOne(wrapper);
	}

	@Override
	public void update(UnionRefundOrder refundOrder) throws Exception {
		if (refundOrder == null) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		unionRefundOrderDao.updateById(refundOrder);
	}
}