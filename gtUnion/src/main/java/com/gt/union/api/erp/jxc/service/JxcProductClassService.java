package com.gt.union.api.erp.jxc.service;

/**
 * 进销存商品分类api
 * @author hongjiye
 * @time 2017-12-06 16:41
 */
public interface JxcProductClassService {

	/**
	 * 根据商家id查询商品分类列表  树形结构
	 * @param busId
	 * @return
	 */
	String listProductClassByBusId(Integer busId);
}
