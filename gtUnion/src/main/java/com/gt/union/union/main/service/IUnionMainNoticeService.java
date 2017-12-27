package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.union.main.entity.UnionMainNotice;

import java.util.List;

/**
 * 联盟公告 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:18:52
 */
public interface IUnionMainNoticeService {
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
     * @param unionMainNoticeList 数据源
     * @param delStatus           删除状态
     * @return List<UnionMainNotice>
     * @throws Exception 统一处理异常
     */
    List<UnionMainNotice> filterByDelStatus(List<UnionMainNotice> unionMainNoticeList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取联盟公告信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainNotice
     * @throws Exception 统一处理异常
     */
    UnionMainNotice getById(Integer id) throws Exception;

    /**
     * 获取未删除的联盟公告信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainNotice
     * @throws Exception 统一处理异常
     */
    UnionMainNotice getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的联盟公告信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainNotice
     * @throws Exception 统一处理异常
     */
    UnionMainNotice getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionMainNoticeList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionMainNotice> unionMainNoticeList) throws Exception;


    /**
     * 获取联盟公告列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMainNotice>
     * @throws Exception 统一处理异常
     */
    List<UnionMainNotice> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的联盟公告列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMainNotice>
     * @throws Exception 统一处理异常
     */
    List<UnionMainNotice> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的联盟公告列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMainNotice>
     * @throws Exception 统一处理异常
     */
    List<UnionMainNotice> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionMainNotice>
     * @throws Exception 统一处理异常
     */
    List<UnionMainNotice> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page<UnionMainNotice>
     * @throws Exception 统一处理异常
     */
    Page<UnionMainNotice> pageSupport(Page page, EntityWrapper<UnionMainNotice> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionMainNotice 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionMainNotice newUnionMainNotice) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionMainNoticeList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionMainNotice> newUnionMainNoticeList) throws Exception;

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
     * @param updateUnionMainNotice 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionMainNotice updateUnionMainNotice) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionMainNoticeList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionMainNotice> updateUnionMainNoticeList) throws Exception;

    // TODO
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 我的联盟-首页-联盟公告
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionMainNotice
     * @throws Exception 统一处理异常
     */
    UnionMainNotice getByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 获取联盟公告信息
     *
     * @param unionId 联盟id
     * @return UnionMainNotice
     * @throws Exception 统一处理异常
     */
    UnionMainNotice getByUnionId(Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 我的联盟-首页-联盟公告-更新
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param content 公告内容
     * @throws Exception 统一处理异常
     */
    void updateContentByBusIdAndUnionId(Integer busId, Integer unionId, String content) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

}