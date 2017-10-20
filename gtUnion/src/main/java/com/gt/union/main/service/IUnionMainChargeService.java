package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainCharge;

import java.util.List;

/**
 * 联盟升级收费 服务接口
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
public interface IUnionMainChargeService extends IService<UnionMainCharge> {

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    /**
     * 根据联盟id和联盟卡类型获取联盟卡信息
     *
     * @param unionId {not null} 联盟卡
     * @param type    {not null} 联盟卡类型
     * @return UnionMainCharge
     * @throws Exception 全局处理异常
     */
    UnionMainCharge getByUnionIdAndType(Integer unionId, Integer type) throws Exception;

    /**
     * 根据联盟id、红黑卡类型和是否启用，获取联盟升级收费信息
     *
     * @param unionId     {not null} 联盟id
     * @param type        {not null} 红黑卡类型
     * @param isAvailable {not null} 是否启用
     * @return UnionMainCharge
     * @throws Exception 全局处理异常
     */
    UnionMainCharge getByUnionIdAndTypeAndIsAvailable(Integer unionId, Integer type, Integer isAvailable) throws Exception;

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    /**
     * 根据id查询对象
     *
     * @param chargeId 对象id
     * @return UnionMainCharge
     * @throws Exception 全局处理异常
     */
    UnionMainCharge getById(Integer chargeId) throws Exception;

    //******************************************* Object As a Service - list *******************************************

    /**
     * 根据联盟id查询对象列表
     *
     * @param unionId 联盟id
     * @return List<UnionMainCharge>
     * @throws Exception 全局处理异常
     */
    List<UnionMainCharge> listByUnionId(Integer unionId) throws Exception;

    //******************************************* Object As a Service - save *******************************************

    /**
     * 新增对象
     *
     * @param newCharge 新增的对象
     * @throws Exception 全局处理异常
     */
    void save(UnionMainCharge newCharge) throws Exception;

    /**
     * 批量新增对象
     *
     * @param newChargeList 批量新增的对象列表
     * @throws Exception 全局处理异常
     */
    void saveBatch(List<UnionMainCharge> newChargeList) throws Exception;

    //******************************************* Object As a Service - remove *****************************************

    /**
     * 根据id删除对象
     *
     * @param chargeId 对象id
     * @throws Exception 全局处理异常
     */
    void removeById(Integer chargeId) throws Exception;

    /**
     * 根据id列表批量删除对象
     *
     * @param chargeIdList 对象id列表
     * @throws Exception 全局处理异常
     */
    void removeBatchById(List<Integer> chargeIdList) throws Exception;

    //******************************************* Object As a Service - update *****************************************

    /**
     * 修改对象
     *
     * @param updateCharge 修改的对象
     * @throws Exception 全局处理异常
     */
    void update(UnionMainCharge updateCharge) throws Exception;

    /**
     * 批量修改对象
     *
     * @param updateChargeList 批量修改的对象列表
     * @throws Exception 全局处理异常
     */
    void updateBatch(List<UnionMainCharge> updateChargeList) throws Exception;
}
