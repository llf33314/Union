package com.gt.union.service.business;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.business.UnionBrokerage;

/**
 * <p>
 * 联盟商家佣金比率 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
public interface IUnionBrokerageService extends IService<UnionBrokerage> {

	/**
	 * 更新佣金比设置
	 * @param unionBrokerage
	 * @throws Exception
	 */
	public void updateByIdAndUnionId(UnionBrokerage unionBrokerage) throws Exception;

    /**
     * 根据联盟id、推荐方、接受方获取商机佣金比信息
     * @param unionId
     * @param fromBusId
     * @param toBusId
     * @return
     * @throws Exception
     */
	public UnionBrokerage getByUnionIdAndFromBusIdAndToBusId(Integer unionId, Integer fromBusId, Integer toBusId) throws Exception;
}
