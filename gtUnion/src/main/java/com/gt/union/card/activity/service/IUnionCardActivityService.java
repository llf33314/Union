package com.gt.union.card.activity.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.vo.CardActivityStatusVO;
import com.gt.union.card.activity.vo.CardActivityVO;

import java.util.List;

/**
 * 活动 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public interface IUnionCardActivityService extends IService<UnionCardActivity> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 根据活动id和联盟id，获取活动信息
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

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：售卡佣金分成管理-活动卡售卡比例设置
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<CardActivityStatusVO>
     * @throws Exception 统一处理异常
     */
    List<CardActivityStatusVO> listCardActivityStatusVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 分页：联盟卡设置-活动卡设置
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<CardActivityVO>
     * @throws Exception 统一处理异常
     */
    List<CardActivityVO> listCardActivityVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    /**
     * 删除：联盟卡设置-活动卡设置
     *
     * @param activityId 活动id
     * @param unionId    联盟id
     * @param busId      商家id
     * @throws Exception 统一处理异常
     */
    void removeByIdAndUnionIdAndBusId(Integer activityId, Integer unionId, Integer busId) throws Exception;

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}