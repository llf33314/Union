package com.gt.union.consume.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.api.entity.param.UnionConsumeParam;
import com.gt.union.api.entity.param.UnionRefundParam;
import com.gt.union.api.entity.result.UnionConsumeResult;
import com.gt.union.api.entity.result.UnionRefundResult;
import com.gt.union.consume.entity.UnionConsume;

/**
 * <p>
 * 消费 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionConsumeService extends IService<UnionConsume> {

	/**
	 * 线上联盟卡核销
	 * @param reqdata
	 * @return
	 */
	UnionConsumeResult consumeByUnionCard(UnionConsumeParam reqdata) throws Exception;

	/**
	 * 联盟退款
	 * @param reqdata
	 * @return
	 */
	UnionRefundResult unionRefund(UnionRefundParam reqdata);
}
