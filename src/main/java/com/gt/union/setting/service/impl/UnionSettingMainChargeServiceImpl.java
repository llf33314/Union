package com.gt.union.setting.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.setting.entity.UnionSettingMainCharge;
import com.gt.union.setting.mapper.UnionSettingMainChargeMapper;
import com.gt.union.setting.service.IUnionSettingMainChargeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 联盟盟主服务收费套餐 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-29
 */
@Service
public class UnionSettingMainChargeServiceImpl extends ServiceImpl<UnionSettingMainChargeMapper, UnionSettingMainCharge> implements IUnionSettingMainChargeService {

	@Override
	public List<UnionSettingMainCharge> listBusLevel(Integer level) {
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.eq("level",level);
		wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		return this.selectList(wrapper);
	}

	@Override
	public UnionSettingMainCharge getById(Integer chargeId) {
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.eq("id",chargeId);
		wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		return this.selectOne(wrapper);
	}
	
}
