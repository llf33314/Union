package com.gt.union.api.client.address;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/22 0022.
 */
public interface AddressService {

	/**
	 * 根据ids获取地址列表
	 * @param param
	 * @return
	 */
	public List<Map> getByIds(Map<String, Object> param);

	/**
	 * 根据city_code列表获取地址列表
	 * @param param
	 * @return
	 */
	public List<Map> getByCityCode(Map<String, Object> param);

}
