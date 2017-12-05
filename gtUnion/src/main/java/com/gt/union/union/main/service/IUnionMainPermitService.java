package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.main.entity.UnionMainPermit;
import com.gt.union.union.main.vo.UnionPermitPayVO;

/**
 * 联盟许可 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:19
 */
public interface IUnionMainPermitService extends IService<UnionMainPermit> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取商家有效的联盟许可
     *
     * @param busId 商家id
     * @return UnionMainPermit
     * @throws Exception 统一处理异常
     */
    UnionMainPermit getValidByBusId(Integer busId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存新许可
     *
     * @param savePermit 新许可
     * @throws Exception 统一处理异常
     */
    void save(UnionMainPermit savePermit) throws Exception;

    /**
     * 保存新购买的许可
     *
     * @param busId     商家id
     * @param packageId 套餐id
     * @return UnionPermitPayVO
     * @throws Exception 统一处理异常
     */
    UnionPermitPayVO saveByBusIdAndPackageId(Integer busId, Integer packageId) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    
    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}