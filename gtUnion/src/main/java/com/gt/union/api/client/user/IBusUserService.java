package com.gt.union.api.client.user;

import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.union.api.client.user.bean.UserUnionAuthority;
import com.gt.util.entity.result.wx.ApiWxApplet;

import java.util.List;
import java.util.Map;

/**
 * 商家信息 服务类api
 *
 * @author hongjiye
 * @time 2017/11/25 0022.
 */
public interface IBusUserService {
    /**
     * 根据商家id获取商家信息
     *
     * @param busId 商家id
     * @return
     */
    BusUser getBusUserById(Integer busId);

    /**
     * 根据账号名称获取商家信息
     *
     * @param name 账号名称
     * @return
     */
    BusUser getBusUserByName(String name);


    /**
     * 根据商家id获取公众号信息
     *
     * @param busId 商家id
     * @return
     */
    WxPublicUsers getWxPublicUserByBusId(Integer busId);

    /**
     * 获取公众号关注二维码永久链接
     *
     * @param publicId 公众id
     * @param busId    商家id
     * @param extendId socket推送业务id
     * @return
     */
    String getWxPublicUserQRCode(Integer publicId, Integer busId, Integer extendId);

    /**
     * 根据商家id获取联盟权限
     *
     * @param busId 商家id
     * @return authority：联盟权限 true：有 false：无  pay：是否需要支付 true：需要支付 false：不需支付  unionVersionName:商家的联盟版本名称  无，盟员版，盟主版
     */
    UserUnionAuthority getUserUnionAuthority(Integer busId);

    /**
     * 根据手机号获取商家信息
     *
     * @param phone 手机号
     * @return
     */
    BusUser getBusUserByPhone(String phone);

    /**
     * 根据商家id和industry获取商家小程序信息
     *
     * @param busId    商家id
     * @param industry 类型
     * @return
     */
    ApiWxApplet getBusIdAndIndustry(Integer busId, Integer industry);

    /**
     * 查询商家是否拥有某个菜单的权限
     *
     * @param busId   商家id
     * @param identis 菜单标识
     * @return
     */
    List<Map> haveMenus(Integer busId, String identis);

}
