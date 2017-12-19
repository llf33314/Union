package com.gt.union.api.client.erp;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.client.erp.vo.ErpModelVO;
import com.gt.union.api.client.erp.vo.ErpServerVO;

import java.util.List;

public interface ErpService {

	/**
	 * 根据商家id获取erp列表
	 * @param busId
	 * @return	erpmodel：erp类型
	 */
	List<ErpModelVO> listErpByBusId(Integer busId);

	/**
	 * 查询erp服务项目
	 * @param shopId        门店id
	 * @param erpModel        erp行业类型
	 * @param search        搜索条件
	 * @param page            分页
	 * @param busId			商家id
	 * @return
	 */
	List<ErpServerVO> listErpServer(Integer shopId, Integer erpModel, String search, Page page, Integer busId);
}
