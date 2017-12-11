package com.gt.union.card.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.vo.CardApplyVO;
import com.gt.union.card.main.vo.CardSocketVO;

import java.util.List;

/**
 * 联盟卡 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public interface IUnionCardService extends IService<UnionCard> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 前台-办理联盟卡-查询联盟和联盟卡
     *
     * @param busId      商家id
     * @param fanId      粉丝id
     * @param optUnionId 联盟id
     * @return CardApplyVO
     * @throws Exception 统一处理异常
     */
    CardApplyVO getCardApplyVOByBusIdAndFanId(Integer busId, Integer fanId, Integer optUnionId) throws Exception;

    /**
     * 根据粉丝id和联盟id，获取折扣卡信息
     *
     * @param fanId   粉丝id
     * @param unionId 联盟id
     * @return UnionCard
     * @throws Exception 统一处理异常
     */
    UnionCard getDiscountCardByFanIdAndUnionId(Integer fanId, Integer unionId) throws Exception;

    /**
     * 根据粉丝id、活动id和联盟id，获取折扣卡信息
     *
     * @param fanId      粉丝id
     * @param activityId 活动id
     * @param unionId    联盟id
     * @return UnionCard
     * @throws Exception 统一处理异常
     */
    UnionCard getActivityCardByFanIdAndActivityIdAndUnionId(Integer fanId, Integer activityId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 根据联盟id，获取有效的联盟卡信息
     *
     * @param unionId 联盟id
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 根据粉丝id和联盟id，获取有效的联盟卡信息
     *
     * @param fanId   粉丝id
     * @param unionId 联盟id
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByFanIdAndUnionId(Integer fanId, Integer unionId) throws Exception;

    /**
     * 根据粉丝id、联盟id和联盟卡类型，获取有效的联盟卡信息
     *
     * @param fanId   粉丝id
     * @param unionId 联盟id
     * @param type    联盟卡类型
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByFanIdAndUnionIdAndType(Integer fanId, Integer unionId, Integer type) throws Exception;

    /**
     * 根据活动id和联盟id，获取活动卡信息
     *
     * @param activityId 活动id
     * @param unionId    联盟id
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listByActivityIdAndUnionId(Integer activityId, Integer unionId) throws Exception;

    /**
     * 根据粉丝id，获取有效的联盟卡信息
     *
     * @param fanId 粉丝id
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByFanId(Integer fanId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存办理联盟卡信息
     *
     * @param busId          商家id
     * @param fanId          粉丝id
     * @param unionId        联盟id
     * @param activityIdList 活动id列表
     * @return CardSocketVO
     * @throws Exception 统一处理异常
     */
    CardSocketVO saveApplyByBusIdAndFanIdAndUnionId(Integer busId, Integer fanId, Integer unionId, List<Integer> activityIdList) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 根据活动id和联盟id，统计活动卡个数
     *
     * @param activityId 活动id
     * @param unionId    联盟id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countByActivityIdAndUnionId(Integer activityId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

    /**
     * 根据粉丝id、联盟id和联盟卡类型，判断是否存在有效的联盟卡信息
     *
     * @param fanId   粉丝id
     * @param unionId 联盟id
     * @param type    联盟卡类型
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidByFanIdAndUnionIdAndType(Integer fanId, Integer unionId, Integer type) throws Exception;

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据联盟卡类型进行过滤
     *
     * @param cardList 数据源
     * @param type     类型
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> filterByType(List<UnionCard> cardList, Integer type) throws Exception;

    /**
     * 根据联盟id进行过滤
     *
     * @param cardList 数据源
     * @param unionId  联盟id
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> filterByUnionId(List<UnionCard> cardList, Integer unionId) throws Exception;
}