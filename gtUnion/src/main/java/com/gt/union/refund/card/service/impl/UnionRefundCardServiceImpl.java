package com.gt.union.refund.card.service.impl;

import com.gt.union.refund.card.dao.IUnionRefundCardDao;
import com.gt.union.refund.card.service.IUnionRefundCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author hongjiye
 * @version 2018-02-02 16:58:00
 */
@Service
public class UnionRefundCardServiceImpl implements IUnionRefundCardService {
    @Autowired
    private IUnionRefundCardDao unionRefundCardDao;

}