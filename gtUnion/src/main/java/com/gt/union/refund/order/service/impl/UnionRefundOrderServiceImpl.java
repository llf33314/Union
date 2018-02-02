package com.gt.union.refund.order.service.impl;

import com.gt.union.refund.order.dao.IUnionRefundOrderDao;
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
}