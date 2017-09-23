package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.main.entity.UnionMainPermitCharge;
import com.gt.union.main.mapper.UnionMainPermitChargeMapper;
import com.gt.union.main.service.IUnionMainPermitChargeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 联盟盟主服务收费套餐 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-23
 */
@Service
public class UnionMainPermitChargeServiceImpl extends ServiceImpl<UnionMainPermitChargeMapper, UnionMainPermitCharge> implements IUnionMainPermitChargeService {

	@Override
	public List<UnionMainPermitCharge> listBusLevel(Integer level) {
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.eq("level",level);
		wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		return this.selectList(wrapper);
	}

	@Override
	public UnionMainPermitCharge getById(Integer chargeId) {
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.eq("id",chargeId);
		wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		return this.selectOne(wrapper);
	}
}
