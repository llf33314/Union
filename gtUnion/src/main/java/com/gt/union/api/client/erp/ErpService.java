package com.gt.union.api.client.erp;

import java.util.List;
import java.util.Map;

public interface ErpService {

	/**
	 * 根据商家id获取erp列表
	 * @param busId
	 * @return
	 */
	List<Map> listErpByBusId(Integer busId);
}
