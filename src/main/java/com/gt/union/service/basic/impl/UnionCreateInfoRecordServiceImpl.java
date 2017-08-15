package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.DateTimeKit;
import com.gt.union.entity.basic.UnionCreateInfoRecord;
import com.gt.union.mapper.basic.UnionCreateInfoRecordMapper;
import com.gt.union.service.basic.IUnionCreateInfoRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 创建联盟服务记录 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-31
 */
@Service
public class UnionCreateInfoRecordServiceImpl extends ServiceImpl<UnionCreateInfoRecordMapper, UnionCreateInfoRecord> implements IUnionCreateInfoRecordService {

	@Override
	public UnionCreateInfoRecord getBusUnion(Integer busId, Integer unionId) {
		EntityWrapper entityWrapper = new EntityWrapper<UnionCreateInfoRecord>();
		entityWrapper.eq("del_status", 0);
		entityWrapper.eq("bus_id",busId);
		if(CommonUtil.isNotEmpty(unionId)){
			entityWrapper.eq("union_id",unionId);
		}
		entityWrapper.gt("period_validity", DateTimeKit.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
		entityWrapper.orderBy("id",false);
		UnionCreateInfoRecord unionCreateInfoRecord = this.selectOne(entityWrapper);
		return unionCreateInfoRecord;
	}
}
