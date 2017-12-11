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
     * 根据id，获取对象
     *
     * @param id id
     * @return UnionCardProjectItem
     * @throws Exception 统一处理异常
     */
    UnionCardProjectItem getById(Integer id) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 根据项目id，获取名称来自接口的优惠信息
     *
     * @param projectId 项目id
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> listItemByProjectId(Integer projectId) throws Exception;

    /**
     * 根据项目id和类型，获取名称来自接口的优惠信息
     *
     * @param projectId 项目id
     * @param type      类型
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> listItemByProjectIdAndType(Integer projectId, Integer type) throws Exception;

    /**
     * 列表：前台-联盟卡消费核销-开启优惠项目-优惠
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
     * 联盟卡设置-活动卡设置-我的活动项目-保存
     *
     * @param busId      商家id
     * @param activityId 活动id
     * @param unionId    联盟id
     * @param vo         表单内容
     * @throws Exception 统一处理异常
     */
    void saveProjectItemVOByBusIdAndActivityIdAndUnionId(Integer busId, Integer activityId, Integer unionId, CardProjectVO vo) throws Exception;

    /**
     * 联盟卡设置-活动卡设置-我的活动项目-提交
     *
     * @param busId      商家id
     * @param activityId 活动id
     * @param unionId    联盟id
     * @param vo         表单内容
     * @throws Exception 统一处理异常
     */
    void commitProjectItemVOByBusIdAndActivityIdAndUnionId(Integer busId, Integer activityId, Integer unionId, CardProjectVO vo) throws Exception;

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
     * 根据联盟id和活动id，统计已提交通过的项目优惠个数;
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