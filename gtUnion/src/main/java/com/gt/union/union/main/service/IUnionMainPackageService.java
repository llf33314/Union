package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.main.entity.UnionMainPackage;
import com.gt.union.union.main.vo.UnionPackageVO;

import java.util.List;

/**
 * 盟主服务套餐 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:16
 */
public interface IUnionMainPackageService extends IService<UnionMainPackage> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取套餐信息
     *
     * @param packageId 套餐id
     * @return UnionMainPackage
     * @throws Exception 统一处理异常
     */
    UnionMainPackage getById(Integer packageId) throws Exception;

    /**
     * 获取套餐信息
     *
     * @param level 等级
     * @return UnionMainPackage
     * @throws Exception 统一处理异常
     */
    UnionMainPackage getByLevel(Integer level) throws Exception;

    /**
     * 我的联盟-创建联盟-购买盟主服务
     *
     * @param busId 商家id
     * @return UnionPackageVO
     * @throws Exception 统一处理异常
     */
    UnionPackageVO getUnionPackageVOByBusId(Integer busId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取套餐列表信息
     *
     * @param level 等级
     * @return UnionMainPackage
     * @throws Exception 统一处理异常
     */
    List<UnionMainPackage> listByLevel(Integer level) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}