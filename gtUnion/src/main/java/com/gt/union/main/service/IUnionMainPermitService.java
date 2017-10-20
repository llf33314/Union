package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.api.bean.session.BusUser;
import com.gt.union.main.entity.UnionMainPermit;

import java.util.List;
import java.util.Map;

/**
 * 联盟许可，盟主服务 服务接口
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
public interface IUnionMainPermitService extends IService<UnionMainPermit> {
    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    /**
     * 根据商家id获取联盟服务许可
     *
     * @param busId {not null} 商家id
     * @return UnionMainPermit
     * @throws Exception 全局处理异常
     */
    UnionMainPermit getByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id和许可id获取联盟服务许可信息
     *
     * @param busId {not null} 商家id
     * @param id    {not null} 联盟许可id
     * @return UnionMainPermit
     * @throws Exception 全局处理异常
     */
    UnionMainPermit getByBusIdAndId(Integer busId, Integer id) throws Exception;

    /**
     * 获取创建联盟的支付二维码
     *
     * @param user     用户
     * @param chargeId 对象id
     * @return Map <String,Object>
     * @throws Exception 全局处理异常
     */
    Map<String, Object> createUnionQRCode(BusUser user, Integer chargeId) throws Exception;

    /**
     * 创建联盟支付成功后回调
     *
     * @param orderNo 订单号
     * @param only    唯一
     * @param payType 类型
     * @throws Exception 全局处理异常
     */
    void payCreateUnionSuccess(String orderNo, String only, Integer payType) throws Exception;

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    /**
     * 获取所有已过期的、但为未删除状态的盟主服务许哭列表信息
     *
     * @return List<UnionMainPermit>
     * @throws Exception 全局处理异常
     */
    List<UnionMainPermit> listExpired() throws Exception;

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    /**
     * 根据商家id判断是否拥有盟主服务许可
     *
     * @param busId {not null} 商家id
     * @return boolean
     * @throws Exception 全局处理异常
     */
    boolean hasUnionMainPermit(Integer busId) throws Exception;

    //******************************************* Object As a Service - get ********************************************

    //******************************************* Object As a Service - list *******************************************

    //******************************************* Object As a Service - save *******************************************

    //******************************************* Object As a Service - remove *****************************************

    //******************************************* Object As a Service - update *****************************************
}
