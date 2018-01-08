package com.gt.union.card.sharing.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.sharing.entity.UnionCardSharingRatio;
import com.gt.union.card.sharing.vo.CardSharingRatioVO;

import java.util.List;

/**
 * 联盟卡售卡分成比例 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public interface IUnionCardSharingRatioService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 获取未删除的售卡分成佣金信息
     *
     * @param unionId    联盟id
     * @param memberId   盟员id
     * @param activityId 活动id
     * @return UnionCardSharingRatio
     * @throws Exception 统一处理异常
     */
    UnionCardSharingRatio getValidByUnionIdAndMemberIdAndActivityId(Integer unionId, Integer memberId, Integer activityId) throws Exception;

    //********************************************* Base On Business - list ********************************************

    /**
     * 获取未删除的售卡分成比例列表信息
     *
     * @param unionId    联盟id
     * @param activityId 活动id
     * @param orderBy    排序字段
     * @param isAsc      是否升序
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> listValidByUnionIdAndActivityId(Integer unionId, Integer activityId, String orderBy, boolean isAsc) throws Exception;

    /**
     * 分页：我的联盟-售卡佣金分成管理-活动卡售卡比例设置-选择活动卡后；列表：我的联盟-售卡佣金分成管理-活动卡售卡比例设置-选择活动卡后-比例设置
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return List<CardSharingRatioVO>
     * @throws Exception 统一处理异常
     */
    List<CardSharingRatioVO> listCardSharingRatioVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception;

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    /**
     * 我的联盟-售卡佣金分成管理-活动卡售卡比例设置-选择活动卡后-比例设置-保存
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @param voList     表单内容
     * @throws Exception 统一处理异常
     */
    void updateByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, List<CardSharingRatioVO> voList) throws Exception;

    /**
     * 自动分配售卡分成比例
     *
     * @param projectList 活动项目列表
     * @throws Exception 统一处理异常
     */
    void autoEqualDivisionRatio(List<UnionCardProject> projectList) throws Exception;

    //********************************************* Base On Business - other *******************************************

    /**
     * 是否存在未删除的售卡分成比例信息
     *
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception;

    /**
     * 是否存在售卡分成比例所有者是已退盟状态的
     *
     * @param ratioList 售卡分成比例列表
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existInvalidMemberId(List<UnionCardSharingRatio> ratioList) throws Exception;

    /**
     * 是否存在售卡分成比例所有者是盟主
     *
     * @param ratioList 售卡分成比例列表
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existUnionOwnerId(List<UnionCardSharingRatio> ratioList) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionCardSharingRatioList 数据源
     * @param delStatus                 删除状态
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> filterByDelStatus(List<UnionCardSharingRatio> unionCardSharingRatioList, Integer delStatus) throws Exception;

    /**
     * 根据活动id进行过滤
     *
     * @param ratioList  数据源
     * @param activityId 活动id
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> filterByActivityId(List<UnionCardSharingRatio> ratioList, Integer activityId) throws Exception;

    /**
     * 过滤掉已退盟的数据
     *
     * @param ratioList 数据源
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> filterInvalidMemberId(List<UnionCardSharingRatio> ratioList) throws Exception;

    /**
     * 根据盟员id进行过滤
     *
     * @param ratioList 数据源
     * @param memberId  盟员id
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> filterByMemberId(List<UnionCardSharingRatio> ratioList, Integer memberId) throws Exception;

    /**
     * 根据联盟id进行过滤
     *
     * @param ratioList 数据源
     * @param unionId   联盟id
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> filterByUnionId(List<UnionCardSharingRatio> ratioList, Integer unionId) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取联盟卡售卡分成比例信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardSharingRatio
     * @throws Exception 统一处理异常
     */
    UnionCardSharingRatio getById(Integer id) throws Exception;

    /**
     * 获取未删除的联盟卡售卡分成比例信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardSharingRatio
     * @throws Exception 统一处理异常
     */
    UnionCardSharingRatio getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的联盟卡售卡分成比例信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardSharingRatio
     * @throws Exception 统一处理异常
     */
    UnionCardSharingRatio getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionCardSharingRatioList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionCardSharingRatio> unionCardSharingRatioList) throws Exception;


    /**
     * 获取联盟卡售卡分成比例列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> listByActivityId(Integer activityId) throws Exception;

    /**
     * 获取未删除的联盟卡售卡分成比例列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> listValidByActivityId(Integer activityId) throws Exception;

    /**
     * 获取已删除的联盟卡售卡分成比例列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> listInvalidByActivityId(Integer activityId) throws Exception;

    /**
     * 获取联盟卡售卡分成比例列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> listByMemberId(Integer memberId) throws Exception;

    /**
     * 获取未删除的联盟卡售卡分成比例列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> listValidByMemberId(Integer memberId) throws Exception;

    /**
     * 获取已删除的联盟卡售卡分成比例列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> listInvalidByMemberId(Integer memberId) throws Exception;

    /**
     * 获取联盟卡售卡分成比例列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的联盟卡售卡分成比例列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的联盟卡售卡分成比例列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionCardSharingRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRatio> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionCardSharingRatio> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionCardSharingRatio 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionCardSharingRatio newUnionCardSharingRatio) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionCardSharingRatioList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionCardSharingRatio> newUnionCardSharingRatioList) throws Exception;

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
     * @param updateUnionCardSharingRatio 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionCardSharingRatio updateUnionCardSharingRatio) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionCardSharingRatioList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionCardSharingRatio> updateUnionCardSharingRatioList) throws Exception;

}