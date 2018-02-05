package com.gt.union.api.client.erp;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.client.erp.vo.ErpTypeVO;
import com.gt.union.api.client.erp.vo.ErpServerVO;

import java.util.List;

/**
 * erp服务类
 * @author hongjiye
 * @time 2018/2/5
 */
public interface ErpService {

	/**
	 * 根据商家id获取erp列表
	 * @param busId
	 * @return	erpmodel：erp类型
	 */
	List<ErpTypeVO> listErpByBusId(Integer busId);

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

	/**
	 * 根据商家id判断是否有erp权限
	 * @param busId		商家id
	 * @return
	 */
	Boolean userHasErpAuthority(Integer busId);
}
