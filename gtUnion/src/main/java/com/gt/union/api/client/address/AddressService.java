package com.gt.union.api.client.address;

import java.util.List;
import java.util.Map;

/**
 * 地址服务api
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
public interface AddressService {

	/**
	 * 根据ids获取地址列表
	 * @param ids	地址ids 逗号隔开
	 * @return
	 */
	List<Map> getByIds(String ids);

	/**
	 * 根据city_code列表获取地址列表
	 * @param city_codes city_code列表 逗号隔开
	 * @return
	 */
	List<Map> getByCityCode(String city_codes);

}
