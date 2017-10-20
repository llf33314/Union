package com.gt.union.api.client.user;

import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.WxPublicUsers;

/**
 * 商家信息 服务类
 * Created by Administrator on 2017/8/22 0022.
 */
public interface IBusUserService {
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
	public WxPublicUsers getWxPublicUserByBusId(Integer busId);

	/**
	 *	获取公众号关注二维码永久链接
	 * @param publicId
	 * @param busId
	 * @return
	 */
	String getWxPublicUserQRCode(Integer publicId, Integer busId);

}
