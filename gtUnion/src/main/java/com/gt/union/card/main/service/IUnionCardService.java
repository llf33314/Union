package com.gt.union.card.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.main.entity.UnionCard;

import java.util.List;

/**
 * 联盟卡 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public interface IUnionCardService extends IService<UnionCard> {
    //***************************************** Domain Driven Design - get *********************************************

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


    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 统计联盟积分
     *
     * @param unionId 联盟id
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double countIntegralByUnionId(Integer unionId) throws Exception;

    /**
     * 统计粉丝的联盟积分
     *
     * @param fanId   粉丝id
     * @param unionId 联盟id
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double countIntegralByFanIdAndUnionId(Integer fanId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据联盟卡类型进行过滤
     *
     * @param cardList 联盟卡列表
     * @param type     类型
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> filterByType(List<UnionCard> cardList, Integer type) throws Exception;
}