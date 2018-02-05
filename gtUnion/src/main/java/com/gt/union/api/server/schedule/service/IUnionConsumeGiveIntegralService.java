package com.gt.union.api.server.schedule.service;

import com.gt.union.common.exception.ParamException;

import java.util.Map;

/**
 * @author hongjiye
 * @time 2018-01-03 16:58
 **/
public interface IUnionConsumeGiveIntegralService {
	/**
	 * 赠送积分
	 * @param param
     * @throws Exception    统一异常处理
	 */
	void giveConsumeIntegral(Map param) throws Exception;
}
