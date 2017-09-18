package com.gt.union.api.client.shop;

import com.gt.util.entity.result.shop.WsWxShopInfoExtend;

import java.util.List;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
public interface ShopService {

	/**
	 * 根据商家id获取门店列表信息
	 * @param busId		商家id
	 * @return
	 */
	List<WsWxShopInfoExtend> listByBusId(Integer busId);
}
