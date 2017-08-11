package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.entity.basic.UnionTransferRecord;
import com.gt.union.mapper.basic.UnionTransferRecordMapper;
import com.gt.union.service.basic.IUnionTransferRecordService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 联盟盟主转移记录 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionTransferRecordServiceImpl extends ServiceImpl<UnionTransferRecordMapper, UnionTransferRecord> implements IUnionTransferRecordService {

	@Override
	public UnionTransferRecord get(Integer unionId, Integer busId) {
		EntityWrapper entityWrapper = new EntityWrapper<UnionTransferRecord>();
		entityWrapper.eq("union_id",unionId);
		entityWrapper.eq("to_bus_id",busId);
		entityWrapper.eq("del_status",0);
		entityWrapper.eq("no_advice",0);
		entityWrapper.eq("confirm_status",0);
		return this.selectOne(entityWrapper);
	}
}
