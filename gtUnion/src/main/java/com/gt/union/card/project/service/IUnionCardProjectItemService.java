package com.gt.union.card.project.service;

import com.baomidou.mybatisplus.service.IService;
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
public interface IUnionCardProjectItemService extends IService<UnionCardProjectItem> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取优惠信息
     *
     * @param id 优惠id
     * @return UnionCardProjectItem
     * @throws Exception 统一处理异常
     */
    UnionCardProjectItem getById(Integer id) throws Exception;

    /**
     * 获取优惠信息
     *
     * @param itemId    优惠id
     * @param projectId 项目id
     * @return UnionCardProjectItem
     * @throws Exception 统一处理异常
     */
    UnionCardProjectItem getByIdAndProjectId(Integer itemId, Integer projectId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取优惠列表信息
     *
     * @param projectId 项目id
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> listByProjectId(Integer projectId) throws Exception;

    /**
     * 获取优惠列表信息
     *
     * @param projectId 项目id
     * @param type      类型
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> listByProjectIdAndType(Integer projectId, Integer type) throws Exception;

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

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 联盟卡设置-活动卡设置-分页-我的活动项目-ERP和非ERP-保存
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @param vo         表单内容
     * @throws Exception 统一处理异常
     */
    void saveProjectItemVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, CardProjectVO vo) throws Exception;

    /**
     * 联盟卡设置-活动卡设置-分页-我的活动项目-ERP和非ERP-提交
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @param vo         表单内容
     * @throws Exception 统一处理异常
     */
    void commitProjectItemVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, CardProjectVO vo) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    /**
     * 批量删除
     *
     * @param idList id列表
     * @throws Exception 统一处理异常
     */
    void removeBatchById(List<Integer> idList) throws Exception;

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 统计项目优惠个数
     *
     * @param projectId 项目id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countByProjectId(Integer projectId) throws Exception;

    /**
     * 统计已提交通过的项目优惠个数;
     *
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countCommittedByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据类型进行过滤
     *
     * @param itemList 数据源
     * @param type     类型
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> filterByType(List<UnionCardProjectItem> itemList, Integer type) throws Exception;

}