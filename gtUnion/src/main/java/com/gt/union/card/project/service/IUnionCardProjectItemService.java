package com.gt.union.card.project.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.vo.CardProjectItemConsumeVO;
import com.gt.union.card.project.vo.CardProjectVO;

import java.util.List;

/**
 * 项目优惠 服务接口
 *
 * @author linweicong
 * @version 2017-11-27 09:55:47
 */
public interface IUnionCardProjectItemService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 获取未删除的优惠信息
     *
     * @param itemId    优惠id
     * @param projectId 项目id
     * @return UnionCardProjectItem
     * @throws Exception 统一处理异常
     */
    UnionCardProjectItem getValidByIdAndProjectId(Integer itemId, Integer projectId) throws Exception;

    //********************************************* Base On Business - list ********************************************

    /**
     * 获取未删除的优惠列表信息
     *
     * @param projectId 项目id
     * @param type      类型
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> listValidByProjectIdAndType(Integer projectId, Integer type) throws Exception;

    /**
     * 获取未删除的优惠列表信息
     * 
     * @param unionId       联盟id
     * @param memberId      盟员id
     * @param activityId    活动id
     * @param projectStatus 项目状态
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> listValidByUnionIdAndMemberIdAndActivityIdAndProjectStatus(Integer unionId, Integer memberId, Integer activityId, Integer projectStatus) throws Exception;

    /**
     * 前台-联盟卡消费核销-开启优惠项目-查询活动项目优惠列表
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return List<CardProjectItemConsumeVO>
     * @throws Exception 统一处理异常
     */
    List<CardProjectItemConsumeVO> listCardProjectItemConsumeVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception;

    //********************************************* Base On Business - save ********************************************

    /**
     * 我的联盟-联盟卡设置-活动卡设置-分页数据-我的活动项目-ERP和非ERP-保存
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @param vo         表单内容
     * @throws Exception 统一处理异常
     */
    void saveProjectItemVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, CardProjectVO vo) throws Exception;

    /**
     * 我的联盟-联盟卡设置-活动卡设置-分页数据-我的活动项目-ERP和非ERP-提交
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @param vo         表单内容
     * @throws Exception 统一处理异常
     */
    void commitProjectItemVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, CardProjectVO vo) throws Exception;

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    /**
     * 统计未删除的已提交通过的项目优惠个数;
     *
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countValidCommittedByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception;

    /**
     * 是否存在未删除的项目优惠;
     *
     * @param unionId       联盟id
     * @param memberId      盟员id
     * @param activityId    活动id
     * @param projectStatus 项目状态
     * @param itemType      优惠类型
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidByUnionIdAndMemberIdAndActivityIdAndProjectStatusAndItemType(Integer unionId, Integer memberId, Integer activityId, Integer projectStatus, Integer itemType) throws Exception;

    /**
     * 是否存在未删除的项目优惠;
     *
     * @param unionId        联盟id
     * @param memberId       盟员id
     * @param activityIdList 活动id列表
     * @param projectStatus  项目状态
     * @param itemType       优惠类型
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidByUnionIdAndMemberIdAndActivityIdListAndProjectStatusAndItemType(Integer unionId, Integer memberId, List<Integer> activityIdList, Integer projectStatus, Integer itemType) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionCardProjectItemList 数据源
     * @param delStatus                删除状态
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> filterByDelStatus(List<UnionCardProjectItem> unionCardProjectItemList, Integer delStatus) throws Exception;

    /**
     * 根据类型进行过滤
     *
     * @param itemList 数据源
     * @param type     类型
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> filterByType(List<UnionCardProjectItem> itemList, Integer type) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取项目优惠信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardProjectItem
     * @throws Exception 统一处理异常
     */
    UnionCardProjectItem getById(Integer id) throws Exception;

    /**
     * 获取未删除的项目优惠信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardProjectItem
     * @throws Exception 统一处理异常
     */
    UnionCardProjectItem getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的项目优惠信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardProjectItem
     * @throws Exception 统一处理异常
     */
    UnionCardProjectItem getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionCardProjectItemList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionCardProjectItem> unionCardProjectItemList) throws Exception;


    /**
     * 获取项目优惠列表信息(by myBatisGenerator)
     *
     * @param projectId projectId
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> listByProjectId(Integer projectId) throws Exception;

    /**
     * 获取未删除的项目优惠列表信息(by myBatisGenerator)
     *
     * @param projectId projectId
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> listValidByProjectId(Integer projectId) throws Exception;

    /**
     * 获取已删除的项目优惠列表信息(by myBatisGenerator)
     *
     * @param projectId projectId
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> listInvalidByProjectId(Integer projectId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionCardProjectItem> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionCardProjectItem 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionCardProjectItem newUnionCardProjectItem) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionCardProjectItemList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionCardProjectItem> newUnionCardProjectItemList) throws Exception;

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
     * @param updateUnionCardProjectItem 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionCardProjectItem updateUnionCardProjectItem) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionCardProjectItemList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionCardProjectItem> updateUnionCardProjectItemList) throws Exception;

}