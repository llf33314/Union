package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.api.bean.session.BusUser;
import com.gt.union.main.entity.UnionMainPermit;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟许可，盟主服务 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMainPermitService extends IService<UnionMainPermit> {
    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据商家id获取联盟服务许可
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    UnionMainPermit getByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id和许可id获取联盟服务许可信息
     *
     * @param busId {not null} 商家id
     * @param id    {not null} 联盟许可id
     * @return
     * @throws Exception
     */
    UnionMainPermit getByBusIdAndId(Integer busId, Integer id) throws Exception;

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 获取所有已过期的、但为未删除状态的盟主服务许哭列表信息
     *
     * @return
     * @throws Exception
     */
    List<UnionMainPermit> listExpired() throws Exception;

    //------------------------------------------------- update --------------------------------------------------------
    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------

    /**
     * 根据商家id判断是否拥有盟主服务许可
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    boolean hasUnionMainPermit(Integer busId) throws Exception;

    /**
     * 获取创建联盟的支付二维码
     * @param user
     * @param chargeId
     * @return
     */
    Map<String,Object> createUnionQRCode(BusUser user, Integer chargeId) throws Exception;

    /**
     * 创建联盟支付成功后回调
	 * @param orderNo
	 * @param only
	 * @param payType
	 */
    void payCreateUnionSuccess(String orderNo, String only, Integer payType) throws Exception;
}
