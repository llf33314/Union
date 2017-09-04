package com.gt.union.service.consume.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.entity.param.UnionConsumeParam;
import com.gt.union.api.entity.param.UnionRefundParam;
import com.gt.union.api.entity.result.UnionConsumeResult;
import com.gt.union.api.entity.result.UnionRefundResult;
import com.gt.union.entity.consume.UnionConsumeServiceRecord;
import com.gt.union.mapper.consume.UnionConsumeServiceRecordMapper;
import com.gt.union.service.consume.IUnionConsumeServiceRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 核销服务记录 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionConsumeServiceRecordServiceImpl extends ServiceImpl<UnionConsumeServiceRecordMapper, UnionConsumeServiceRecord> implements IUnionConsumeServiceRecordService {

	@Override
	public List<UnionConsumeServiceRecord> getListGrouyByServiceId(List<Integer> serviceIds) {
		EntityWrapper entityWrapper = new EntityWrapper<UnionConsumeServiceRecord>();
		entityWrapper.in("service_id",serviceIds);
		entityWrapper.groupBy("service_id");
		List<UnionConsumeServiceRecord> records = this.selectList(entityWrapper);
		return records;
	}


	@Override
	public UnionConsumeResult consumeByUnionCard(UnionConsumeParam unionConsumeParam) {
		return null;
	}


	@Override
	public UnionRefundResult unionRefund(UnionRefundParam unionRefundParam) {
		return null;
	}
}
