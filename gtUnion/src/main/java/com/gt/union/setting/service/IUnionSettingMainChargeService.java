package com.gt.union.setting.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.setting.entity.UnionSettingMainCharge;

import java.util.List;

/**
 * <p>
 * 联盟盟主服务收费套餐 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-29
 */
public interface IUnionSettingMainChargeService extends IService<UnionSettingMainCharge> {
	/**
	 * 根据商家等级获取创建联盟套餐列表
	 * @param level
	 * @return
	 */
	List<UnionSettingMainCharge> listBusLevel(Integer level);

	/**
	 * 根据id获取
	 * @param chargeId
	 * @return
	 */
	UnionSettingMainCharge getById(Integer chargeId);
}
