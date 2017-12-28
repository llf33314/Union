package com.gt.union.card.activity.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.vo.*;

import java.util.List;

/**
 * 活动 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public interface IUnionCardActivityService {
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
     * @param unionCardActivityList 数据源
     * @param delStatus             删除状态
     * @return List<UnionCardActivity>
     * @throws Exception 统一处理异常
     */
    List<UnionCardActivity> filterByDelStatus(List<UnionCardActivity> unionCardActivityList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取活动信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardActivity
     * @throws Exception 统一处理异常
     */
    UnionCardActivity getById(Integer id) throws Exception;

    /**
     * 获取未删除的活动信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardActivity
     * @throws Exception 统一处理异常
     */
    UnionCardActivity getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的活动信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardActivity
     * @throws Exception 统一处理异常
     */
    UnionCardActivity getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionCardActivityList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionCardActivity> unionCardActivityList) throws Exception;


    /**
     * 获取活动列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardActivity>
     * @throws Exception 统一处理异常
     */
    List<UnionCardActivity> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的活动列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardActivity>
     * @throws Exception 统一处理异常
     */
    List<UnionCardActivity> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的活动列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardActivity>
     * @throws Exception 统一处理异常
     */
    List<UnionCardActivity> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionCardActivity>
     * @throws Exception 统一处理异常
     */
    List<UnionCardActivity> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionCardActivity> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionCardActivity 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionCardActivity newUnionCardActivity) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionCardActivityList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionCardActivity> newUnionCardActivityList) throws Exception;

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
     * @param updateUnionCardActivity 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionCardActivity updateUnionCardActivity) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionCardActivityList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionCardActivity> updateUnionCardActivityList) throws Exception;

    // TODO

    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取活动信息
     *
     * @param activityId 活动id
     * @param unionId    联盟id
     * @return UnionCardActivity
     * @throws Exception 统一处理异常
     */
    UnionCardActivity getByIdAndUnionId(Integer activityId, Integer unionId) throws Exception;

    /**
     * 获取活动状态
     *
     * @param activity 活动
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer getStatus(UnionCardActivity activity) throws Exception;

    /**
     * 前台-办理联盟卡-查询联盟和联盟卡-查询联盟卡活动
     *
     * @param busId      商家id
     * @param activityId 活动id
     * @param unionId    联盟id
     * @return CardActivityApplyVO
     * @throws Exception 统一处理异常
     */
    CardActivityApplyVO getCardActivityApplyVOByBusIdAndIdAndUnionId(Integer busId, Integer activityId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：我的联盟-售卡佣金分成管理-活动卡售卡比例设置-选择活动卡
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<CardActivityStatusVO>
     * @throws Exception 统一处理异常
     */
    List<CardActivityStatusVO> listCardActivityStatusVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 分页：我的联盟-联盟卡设置-活动卡设置
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<CardActivityVO>
     * @throws Exception 统一处理异常
     */
    List<CardActivityVO> listCardActivityVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 前台-联盟卡消费核销-开启优惠项目-查询活动卡列表
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param fanId   粉丝id
     * @return List<CardActivityConsumeVO>
     * @throws Exception 统一处理异常
     */
    List<CardActivityConsumeVO> listCardActivityConsumeVOByBusIdAndUnionIdAndFanId(Integer busId, Integer unionId, Integer fanId) throws Exception;

    /**
     * 获取活动卡列表信息
     *
     * @param unionId 联盟id
     * @param status  活动卡状态
     * @return List<UnionCardActivity>
     * @throws Exception 统一处理异常
     */
    List<UnionCardActivity> listByUnionIdAndStatus(Integer unionId, Integer status) throws Exception;

    /**
     * 前台-办理联盟卡-查询联盟和联盟卡-查询联盟卡活动-查询服务项目数
     *
     * @param busId      商家id
     * @param activityId 活动id
     * @param unionId    联盟id
     * @return List<CardActivityApplyItemVO>
     * @throws Exception 统一处理异常
     */
    List<CardActivityApplyItemVO> listCardActivityApplyItemVOByBusIdAndIdAndUnionId(Integer busId, Integer activityId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 我的联盟-联盟卡设置-活动卡设置-新增活动卡
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param vo      表单内容
     * @throws Exception 统一处理异常
     */
    void saveByBusIdAndUnionId(Integer busId, Integer unionId, UnionCardActivity vo) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    /**
     * 我的联盟-联盟卡设置-活动卡设置-分页数据-删除
     *
     * @param busId      商家id
     * @param activityId 活动id
     * @param unionId    联盟id
     * @throws Exception 统一处理异常
     */
    void removeByBusIdAndIdAndUnionId(Integer busId, Integer activityId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}