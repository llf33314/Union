package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.union.main.entity.UnionMainPackage;
import com.gt.union.union.main.vo.UnionPackageVO;

import java.util.List;

/**
 * 盟主服务套餐 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:16
 */
public interface IUnionMainPackageService {
    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionMainPackageList 数据源
     * @param delStatus            删除状态
     * @return List<UnionMainPackage>
     * @throws Exception 统一处理异常
     */
    List<UnionMainPackage> filterByDelStatus(List<UnionMainPackage> unionMainPackageList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取盟主服务套餐信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainPackage
     * @throws Exception 统一处理异常
     */
    UnionMainPackage getById(Integer id) throws Exception;

    /**
     * 获取未删除的盟主服务套餐信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainPackage
     * @throws Exception 统一处理异常
     */
    UnionMainPackage getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的盟主服务套餐信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainPackage
     * @throws Exception 统一处理异常
     */
    UnionMainPackage getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionMainPackageList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionMainPackage> unionMainPackageList) throws Exception;


    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionMainPackage>
     * @throws Exception 统一处理异常
     */
    List<UnionMainPackage> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page<UnionMainPackage>
     * @throws Exception 统一处理异常
     */
    Page<UnionMainPackage> pageSupport(Page page, EntityWrapper<UnionMainPackage> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionMainPackage 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionMainPackage newUnionMainPackage) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionMainPackageList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionMainPackage> newUnionMainPackageList) throws Exception;

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
     * @param updateUnionMainPackage 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionMainPackage updateUnionMainPackage) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionMainPackageList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionMainPackage> updateUnionMainPackageList) throws Exception;

    // TODO

    //***************************************** Domain Driven Design - get *********************************************


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