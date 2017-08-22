package com.gt.union.service.business;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.business.UnionBrokerage;
import com.gt.union.vo.business.UnionBrokerageVO;

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
     * 更新或新增商机佣金比例
     * @param unionBrokerageVO
     * @param fromBusId
     * @throws Exception
     */
	public void updateOrSave(UnionBrokerageVO unionBrokerageVO, Integer fromBusId) throws Exception;

    /**
     * 根据联盟id、推荐方、接受方获取商机佣金比信息
     * @param unionId
     * @param fromBusId
     * @param toBusId
     * @return
     * @throws Exception
     */
	public UnionBrokerage getByUnionIdAndFromBusIdAndToBusId(Integer unionId, Integer fromBusId, Integer toBusId) throws Exception;

    /**
     * 根据联盟id和比例设置方商家id获取商机佣金比设置
     * @param page
     * @param unionId
     * @param fromBusId
     * @return
     * @throws Exception
     */
	public Page listMapByUnionIdAndFromBusId(Page page, Integer unionId, Integer fromBusId) throws Exception;
}
