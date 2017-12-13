package com.gt.union.card.activity.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.vo.*;

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
     * 售卡佣金分成管理-活动卡售卡比例设置-分页
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<CardActivityStatusVO>
     * @throws Exception 统一处理异常
     */
    List<CardActivityStatusVO> listCardActivityStatusVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 联盟卡设置-活动卡设置-分页
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
     * 联盟卡设置-活动卡设置-新增活动卡
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param vo      表单内容
     * @throws Exception 统一处理异常
     */
    void saveByBusIdAndUnionId(Integer busId, Integer unionId, UnionCardActivity vo) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    /**
     * 联盟卡设置-活动卡设置-分页-删除
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