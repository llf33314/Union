package com.gt.union.card.sharing.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.sharing.entity.UnionCardSharingRatio;
import com.gt.union.card.sharing.vo.CardSharingRatioVO;

import java.util.List;

/**
 * 联盟卡售卡分成比例 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public interface IUnionCardSharingRatioService extends IService<UnionCardSharingRatio> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 根据活动id、盟员id和联盟id，获取售卡分成佣金信息
     *
     * @param activityId 活动id
     * @param memberId   盟员id
     * @param unionId    联盟id
     * @return UnionCardSharingRatio
     * @throws Exception 统一处理异常
     */
    UnionCardSharingRatio getByActivityIdAndMemberIdAndUnionId(Integer activityId, Integer memberId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：售卡佣金分成管理-活动卡售卡分成比例-查看比例
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return List<CardSharingRatioVO>
     * @throws Exception 统一处理异常
     */
    List<CardSharingRatioVO> listCardSharingRatioVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 批量更新：售卡佣金分成管理-活动卡售卡分成比例-查看比例-比例设置
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @param voList     表单内容
     * @throws Exception 统一处理异常
     */
    void updateRatioByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, List<CardSharingRatioVO> voList) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

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

}