package com.gt.union.refund.opportunity.service.impl;

import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.refund.opportunity.dao.IUnionRefundOpportunityDao;
import com.gt.union.refund.opportunity.entity.UnionRefundOpportunity;
import com.gt.union.refund.opportunity.service.IUnionRefundOpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

	@Override
	public void saveBatch(List<UnionRefundOpportunity> refundOpportunityList) throws Exception {
		if (refundOpportunityList == null) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}

		unionRefundOpportunityDao.insertBatch(refundOpportunityList);
	}
}