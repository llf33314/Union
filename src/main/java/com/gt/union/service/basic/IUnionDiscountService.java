package com.gt.union.service.basic;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionDiscount;

/**
 * <p>
 * 联盟商家折扣 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface IUnionDiscountService extends IService<UnionDiscount> {
    /**
     * 设置商家折扣
     * @param unionId
     * @param fromBusId
     * @param toBusId
     * @param discount
     * @throws Exception
     */
	void updateUnionDiscount(Integer unionId, Integer fromBusId, Integer toBusId, Double discount) throws Exception;

	/**
	 * 判断是否存在
	 * @param wrapper
	 * @return
	 * @throws Exception
	 */
	boolean isExistUnionDiscount(Wrapper wrapper) throws Exception;
}
