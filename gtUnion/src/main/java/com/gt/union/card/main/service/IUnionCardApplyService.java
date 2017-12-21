package com.gt.union.card.main.service;

import com.gt.union.union.main.vo.UnionPayVO;

/**
 * @author hongjiye
 * @time 2017-12-21 10:11
 **/
public interface IUnionCardApplyService {

	UnionPayVO unionCardApply(String orderNo, Double payMoneySum, Integer busId);
}
