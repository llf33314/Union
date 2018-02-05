package com.gt.union.api.server.consume.service;

import com.gt.union.api.entity.param.UnionConsumeParam;

/**
 * @author hongjiye
 * @time 2017-12-22 10:56
 **/
public interface IUnionConsumeApiService {

	/**
	 * 联盟卡核销
	 * @param reqdata
	 * @return
     * @throws Exception    统一异常处理
	 */
	void consumeByUnionCard(UnionConsumeParam reqdata) throws Exception;

	/**
	 * 联盟卡核销退款
	 * @param orderNo		订单号
	 * @param model			行业模型
	 * @return
     * @throws Exception    统一异常处理
	 */
	void unionConsumeRefund(String orderNo, Integer model) throws Exception;
}
