package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.main.entity.UnionMainPermit;
import com.gt.union.union.main.vo.UnionPayVO;

/**
 * 联盟许可 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:19
 */
public interface IUnionMainPermitService extends IService<UnionMainPermit> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取有效的联盟许可信息
     *
     * @param busId 商家id
     * @return UnionMainPermit
     * @throws Exception 统一处理异常
     */
    UnionMainPermit getValidByBusId(Integer busId) throws Exception;

    /**
     * 获取联盟许可信息
     *
     * @param sysOrderNo 订单号
     * @return UnionMainPermit
     * @throws Exception 统一处理异常
     */
    UnionMainPermit getBySysOrderNo(String sysOrderNo) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存
     *
     * @param savePermit 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionMainPermit savePermit) throws Exception;

    /**
     * 我的联盟-创建联盟-购买盟主服务-支付
     *
     * @param busId     商家id
     * @param packageId 套餐id
     * @return UnionPayVO
     * @throws Exception 统一处理异常
     */
    UnionPayVO saveByBusIdAndPackageId(Integer busId, Integer packageId) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 创建联盟-购买盟主服务-支付-回调
     *
     * @param orderNo    订单号
     * @param socketKey  socket关键字
     * @param payType    支付类型
     * @param payOrderNo 支付订单号
     * @param isSuccess  是否成功
     * @return String 返回结果
     */
    String updateCallbackByOrderNo(String orderNo, String socketKey, String payType, String payOrderNo, Integer isSuccess);

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}