package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.main.entity.UnionMain;

/**
 * 联盟 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:16
 */
public interface IUnionMainService extends IService<UnionMain> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 根据id获取联盟对象
     *
     * @param unionId 联盟id
     * @return UnionMain
     * @throws Exception 统一处理异常
     */
    UnionMain getById(Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    /**
     * 判断联盟有效性
     *
     * @param unionId 联盟id
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean isUnionValid(Integer unionId) throws Exception;

    /**
     * 判断联盟有效性
     *
     * @param union 联盟
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean isUnionValid(UnionMain union) throws Exception;

}