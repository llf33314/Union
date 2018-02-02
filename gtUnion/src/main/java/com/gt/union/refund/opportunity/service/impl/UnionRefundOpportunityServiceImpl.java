package com.gt.union.refund.opportunity.service.impl;

import com.gt.union.refund.opportunity.dao.IUnionRefundOpportunityDao;
import com.gt.union.refund.opportunity.service.IUnionRefundOpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author hongjiye
 * @version 2018-02-02 16:58:00
 */
@Service
public class UnionRefundOpportunityServiceImpl implements IUnionRefundOpportunityService {
    @Autowired
    private IUnionRefundOpportunityDao unionRefundOpportunityDao;
}