package com.gt.union.api.client.shop;

import com.gt.util.entity.result.shop.WsWxShopInfoExtend;

import java.util.List;
import java.util.Map;

/**
 * 门店服务api
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0018.
 */
public interface ShopService {

	/**
	 * 根据商家id获取门店列表信息
	 * @param busId        商家id
	 * @return
	 */
	List<Map<String, Object>> listByBusId(Integer busId);

	/**
	 * 根据门店id列表获取门店列表信息
	 * @param list		门店id列表
	 * @return
	 */
	List<WsWxShopInfoExtend> listByIds(List<Integer> list);
}
