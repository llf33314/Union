package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainPermitCharge;

import java.util.List;

/**
 * <p>
 * 联盟盟主服务收费套餐 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-23
 */
public interface IUnionMainPermitChargeService extends IService<UnionMainPermitCharge> {

	/**
	 * 根据商家等级获取创建联盟套餐列表
	 * @param level
	 * @return
	 */
	List<UnionMainPermitCharge> listBusLevel(Integer level);

	/**
	 * 根据id获取
	 * @param chargeId
	 * @return
	 */
	UnionMainPermitCharge getById(Integer chargeId);
}
