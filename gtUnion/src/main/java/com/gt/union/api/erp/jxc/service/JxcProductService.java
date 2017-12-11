package com.gt.union.api.erp.jxc.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.erp.jxc.entity.JxcProduct;

import java.util.List;

/**
 * 进销存商品api
 * @author hongjiye
 * @time 2017-12-07 14:08
 *
 **/
public interface JxcProductService {

	/**
	 * 根据门店id和商品分类id和搜索条件(名称/条码/编码/全拼码) 分页查询商品
	 * @param shopId        门店id
	 * @param classId        商品分类id
	 * @param search        搜索条件(名称/条码/编码/全拼码)
	 * @param pageIndex        查询页
	 * @param pageCount        每页查询条数
	 * @return
	 */
	Page<List<JxcProduct>> listProductByShopIdAndClassIdAndSearchPage(Integer shopId, Integer classId, String search, Integer pageIndex, Integer pageCount);

}
