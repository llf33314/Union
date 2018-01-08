package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.union.main.entity.UnionMainDict;

import java.util.List;

/**
 * 联盟入盟申请必填信息 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
public interface IUnionMainDictService {
    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    /**
     * 获取未删除的入盟必填字段列表信息
     *
     * @param unionId unionId 联盟id
     * @return List<String>
     * @throws Exception 统一处理异常
     */
    List<String> listValidItemKeyByUnionId(Integer unionId) throws Exception;

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionMainDictList 数据源
     * @param delStatus         删除状态
     * @return List<UnionMainDict>
     * @throws Exception 统一处理异常
     */
    List<UnionMainDict> filterByDelStatus(List<UnionMainDict> unionMainDictList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取联盟入盟申请必填信息信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainDict
     * @throws Exception 统一处理异常
     */
    UnionMainDict getById(Integer id) throws Exception;

    /**
     * 获取未删除的联盟入盟申请必填信息信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainDict
     * @throws Exception 统一处理异常
     */
    UnionMainDict getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的联盟入盟申请必填信息信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainDict
     * @throws Exception 统一处理异常
     */
    UnionMainDict getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionMainDictList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionMainDict> unionMainDictList) throws Exception;


    /**
     * 获取联盟入盟申请必填信息列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMainDict>
     * @throws Exception 统一处理异常
     */
    List<UnionMainDict> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的联盟入盟申请必填信息列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMainDict>
     * @throws Exception 统一处理异常
     */
    List<UnionMainDict> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的联盟入盟申请必填信息列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMainDict>
     * @throws Exception 统一处理异常
     */
    List<UnionMainDict> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionMainDict>
     * @throws Exception 统一处理异常
     */
    List<UnionMainDict> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionMainDict> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionMainDict 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionMainDict newUnionMainDict) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionMainDictList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionMainDict> newUnionMainDictList) throws Exception;

    //****************************************** Object As a Service - remove ******************************************

    /**
     * 移除(by myBatisGenerator)
     *
     * @param id 移除内容
     * @throws Exception 统一处理异常
     */
    void removeById(Integer id) throws Exception;

    /**
     * 批量移除(by myBatisGenerator)
     *
     * @param idList 移除内容
     * @throws Exception 统一处理异常
     */
    void removeBatchById(List<Integer> idList) throws Exception;

    //****************************************** Object As a Service - update ******************************************

    /**
     * 更新(by myBatisGenerator)
     *
     * @param updateUnionMainDict 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionMainDict updateUnionMainDict) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionMainDictList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionMainDict> updateUnionMainDictList) throws Exception;
    
}