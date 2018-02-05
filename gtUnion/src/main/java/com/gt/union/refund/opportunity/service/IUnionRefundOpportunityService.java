package com.gt.union.refund.opportunity.service;

import com.gt.union.refund.opportunity.entity.UnionRefundOpportunity;

import java.util.List;

/**
 * 服务接口
 *
 * @author hongjiye
 * @version 2018-02-02 16:58:00
 */
public interface IUnionRefundOpportunityService {

	/**
	 * 批量插入商机退款列表
	 * @param refundOpportunityList
     * @throws Exception    统一异常处理
	 */
	void saveBatch(List<UnionRefundOpportunity> refundOpportunityList) throws Exception;
}
