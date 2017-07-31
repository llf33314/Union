package com.gt.union.service.business;

import com.baomidou.mybatisplus.plugins.Page;
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

	Page selectUnionBrokerageList(Page page, Integer unionId);
}
