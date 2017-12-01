package com.gt.union.api.client.user;

import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.WxPublicUsers;

import java.util.Map;

/**
 * 商家信息 服务类api
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
public interface IBusUserService {
	/**
	 * 根据商家id获取商家信息
	 * @param busId	商家id
	 * @return
	 */
	BusUser getBusUserById(Integer busId);

	/**
	 * 根据账号名称获取商家信息
	 * @param name	账号名称
	 * @return
	 */
	BusUser getBusUserByName(String name);


	/**
	 * 根据商家id获取公众号信息
	 * @param busId		商家id
	 * @return
	 */
	WxPublicUsers getWxPublicUserByBusId(Integer busId);

	/**
	 *	获取公众号关注二维码永久链接
	 * @param publicId	公众id
	 * @param busId		商家id
	 * @return
	 */
	String getWxPublicUserQRCode(Integer publicId, Integer busId);

	/**
	 * 根据商家id获取联盟权限
	 * @param busId		商家id
	 * @return authority：联盟权限 1：有 0：无  pay：是否支付 1：需要支付 0：不需支付  versionName:商家的联盟版本名称  无，盟员版，盟主版
	 */
	Map<String,Object> getUserUnionAuthority(Integer busId);

	/**
	 * 根据手机号获取商家信息
	 * @param phone		手机号
	 * @return
	 */
	BusUser getBusUserByPhone(String phone);

}
