package com.gt.union.card.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.main.entity.UnionCardIntegral;

import java.util.List;

/**
 * 联盟积分 服务接口
 *
 * @author linweicong
 * @version 2017-09-01 11:34:16
 */
public interface IUnionCardIntegralService extends IService<UnionCardIntegral> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 根据粉丝id和联盟id，获取联盟积分信息
     *
     * @param fanId   粉丝id
     * @param unionId 联盟id
     * @return UnionCardIntegral
     * @throws Exception 统一处理异常
     */
    UnionCardIntegral getByFanIdAndUnionId(Integer fanId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 根据粉丝id和联盟id，获取联盟积分信息
     *
     * @param fanId   粉丝id
     * @param unionId 联盟id
     * @return UnionCardIntegral
     * @throws Exception 统一处理异常
     */
    List<UnionCardIntegral> listByFanIdAndUnionId(Integer fanId, Integer unionId) throws Exception;

    /**
     * 根据粉丝id，获取联盟积分信息
     *
     * @param fanId 粉丝id
     * @return UnionCardIntegral
     * @throws Exception 统一处理异常
     */
    List<UnionCardIntegral> listByFanId(Integer fanId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存
     *
     * @param newUnionCardIntegral 内容
     * @throws Exception 统一处理异常
     */
    void save(UnionCardIntegral newUnionCardIntegral) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 更新
     *
     * @param updateUnionCardIntegral 内容
     * @throws Exception 统一处理异常
     */
    void update(UnionCardIntegral updateUnionCardIntegral) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 根据粉丝id和联盟id，统计积分信息
     *
     * @param fanId   粉丝id
     * @param unionId 联盟id
     * @return UnionCardIntegral
     * @throws Exception 统一处理异常
     */
    Double countIntegralByFanIdAndUnionId(Integer fanId, Integer unionId) throws Exception;

    /**
     * 根据粉丝id，获取积分信息
     *
     * @param fanId 粉丝id
     * @return UnionCardIntegral
     * @throws Exception 统一处理异常
     */
    Double countIntegralByFanId(Integer fanId) throws Exception;

    /**
     * 根据联盟id，获取积分信息
     *
     * @param unionId 联盟id
     * @return UnionCardIntegral
     * @throws Exception 统一处理异常
     */
    Double countIntegralByUnionId(Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据联盟id进行过滤
     *
     * @param integralList 数据源
     * @param unionId      联盟id
     * @return List<UnionCardIntegral>
     * @throws Exception 统一处理异常
     */
    List<UnionCardIntegral> filterByUnionId(List<UnionCardIntegral> integralList, Integer unionId) throws Exception;

}