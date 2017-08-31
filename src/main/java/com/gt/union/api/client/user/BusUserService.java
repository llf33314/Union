package com.gt.union.api.client.user;

import com.gt.union.entity.common.BusUser;

import java.util.Map;

/**
 * 商家信息 服务类
 * Created by Administrator on 2017/8/22 0022.
 */
public interface BusUserService {
	/**
	 * 根据id获取商家信息
	 * @param id
	 * @return
	 */
	public BusUser getBusUserById(Integer id);

	/**
	 * 根据账号名称获取商家信息
	 * @param name
	 * @return
	 */
	public BusUser getBusUserByName(String name);


	/**
	 * 根据商家id获取公众号信箱
	 * @param busId
	 * @return
	 */
	public Map<String,Object> getWxPublicUserByBusId(Integer busId);
}
