package com.gt.union.card.main.service;

import com.gt.union.union.main.vo.UnionPayVO;

import java.util.List;

/**
 * @author hongjiye
 * @time 2017-12-21 10:11
 **/
public interface IUnionCardApplyService {

	/**
	 * 办理联盟卡
	 * @param orderNo        订单号
	 * @param payMoneySum    支付金额
	 * @param busId            商家id
	 * @param unionId
	 * @param activityIdList
	 * @return
	 */
	UnionPayVO unionCardApply(String orderNo, Double payMoneySum, Integer busId, Integer unionId, List<Integer> activityIdList);
}
