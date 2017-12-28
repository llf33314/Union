package com.gt.union.card.main.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.main.entity.UnionCardRecord;

import java.util.List;

/**
 * 联盟卡购买记录 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public interface IUnionCardRecordService {
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
     * @param unionCardRecordList 数据源
     * @param delStatus           删除状态
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> filterByDelStatus(List<UnionCardRecord> unionCardRecordList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取联盟卡购买记录信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardRecord
     * @throws Exception 统一处理异常
     */
    UnionCardRecord getById(Integer id) throws Exception;

    /**
     * 获取未删除的联盟卡购买记录信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardRecord
     * @throws Exception 统一处理异常
     */
    UnionCardRecord getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的联盟卡购买记录信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardRecord
     * @throws Exception 统一处理异常
     */
    UnionCardRecord getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionCardRecordList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionCardRecord> unionCardRecordList) throws Exception;


    /**
     * 获取联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listByActivityId(Integer activityId) throws Exception;

    /**
     * 获取未删除的联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listValidByActivityId(Integer activityId) throws Exception;

    /**
     * 获取已删除的联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listInvalidByActivityId(Integer activityId) throws Exception;

    /**
     * 获取联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param cardId cardId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listByCardId(Integer cardId) throws Exception;

    /**
     * 获取未删除的联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param cardId cardId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listValidByCardId(Integer cardId) throws Exception;

    /**
     * 获取已删除的联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param cardId cardId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listInvalidByCardId(Integer cardId) throws Exception;

    /**
     * 获取联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listByFanId(Integer fanId) throws Exception;

    /**
     * 获取未删除的联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listValidByFanId(Integer fanId) throws Exception;

    /**
     * 获取已删除的联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listInvalidByFanId(Integer fanId) throws Exception;

    /**
     * 获取联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listByMemberId(Integer memberId) throws Exception;

    /**
     * 获取未删除的联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listValidByMemberId(Integer memberId) throws Exception;

    /**
     * 获取已删除的联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listInvalidByMemberId(Integer memberId) throws Exception;

    /**
     * 获取联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的联盟卡购买记录列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionCardRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionCardRecord> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionCardRecord 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionCardRecord newUnionCardRecord) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionCardRecordList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionCardRecord> newUnionCardRecordList) throws Exception;

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
     * @param updateUnionCardRecord 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionCardRecord updateUnionCardRecord) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionCardRecordList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionCardRecord> updateUnionCardRecordList) throws Exception;

    // TODO

    //***************************************** Domain Driven Design - get *********************************************


    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取联盟卡购买记录列表信息
     *
     * @param orderNo 订单号
     * @return UnionCardRecord
     * @throws Exception 统一处理异常
     */
    List<UnionCardRecord> listByOrderNo(String orderNo) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************


    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************


    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}