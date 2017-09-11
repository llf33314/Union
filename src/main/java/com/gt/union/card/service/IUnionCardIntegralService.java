package com.gt.union.card.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.entity.UnionCardIntegral;

/**
 * <p>
 * 联盟卡积分 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionCardIntegralService extends IService<UnionCardIntegral> {
    /**
     * 根据联盟id和积分状态，统计总积分数
     *
     * @param unionId {not null} 联盟id
     * @param status  {not null} 积分状态
     * @return
     * @throws Exception
     */
    Double sumCardIntegralByUnionIdAndStatus(Integer unionId, Integer status) throws Exception;

    /**
     * 根据联盟id，获取联盟剩余有效的积分数，即收入积分-支出积分
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    Double getCardIntegralProfitByUnionId(Integer unionId) throws Exception;
}
