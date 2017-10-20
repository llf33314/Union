package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainDict;

import java.util.List;

/**
 * 联盟设置申请填写信息 服务接口
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
public interface IUnionMainDictService extends IService<UnionMainDict> {
    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    /**
     * 根据联盟id删除联盟申请填写信息设置
     *
     * @param unionId {not null} 联盟id
     * @throws Exception 全局处理异常
     */
    void removeByUnionId(Integer unionId) throws Exception;

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    /**
     * 根据id查询对象
     *
     * @param dictId 对象id
     * @return UnionMainDict
     * @throws Exception 全局处理异常
     */
    UnionMainDict getById(Integer dictId) throws Exception;

    //******************************************* Object As a Service - list *******************************************

    /**
     * 根据联盟id查询对象列表
     *
     * @param unionId 联盟id
     * @return List<UnionMainDict>
     * @throws Exception 全局处理异常
     */
    List<UnionMainDict> listByUnionId(Integer unionId) throws Exception;

    //******************************************* Object As a Service - save *******************************************

    /**
     * 新增对象
     *
     * @param newDict 新增的对象
     * @throws Exception 全局处理异常
     */
    void save(UnionMainDict newDict) throws Exception;

    /**
     * 批量新增对象
     *
     * @param newDictList 批量新增的对象列表
     * @throws Exception 全局处理异常
     */
    void saveBatch(List<UnionMainDict> newDictList) throws Exception;

    //******************************************* Object As a Service - remove *****************************************

    /**
     * 根据id删除对象
     *
     * @param dictId 对象id
     * @throws Exception 全局处理异常
     */
    void removeById(Integer dictId) throws Exception;

    /**
     * 根据id列表批量删除对象
     *
     * @param dictIdList 对象id列表
     * @throws Exception 全局处理异常
     */
    void removeBatchById(List<Integer> dictIdList) throws Exception;

    //******************************************* Object As a Service - update *****************************************

    /**
     * 修改对象
     *
     * @param updateDict 修改的对象
     * @throws Exception 全局处理异常
     */
    void update(UnionMainDict updateDict) throws Exception;

    /**
     * 批量修改对象
     *
     * @param updateDictList 批量修改的对象列表
     * @throws Exception 全局处理异常
     */
    void updateBatch(List<UnionMainDict> updateDictList) throws Exception;
}
