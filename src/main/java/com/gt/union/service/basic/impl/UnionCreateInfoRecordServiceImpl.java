package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionCreateInfoRecordConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.entity.basic.UnionCreateInfoRecord;
import com.gt.union.mapper.basic.UnionCreateInfoRecordMapper;
import com.gt.union.service.basic.IUnionCreateInfoRecordService;
import org.springframework.stereotype.Service;

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
    private static final String GET_BUSID = "UnionCreateInfoRecordServiceImpl.getByBusId()";

	@Override
	public UnionCreateInfoRecord getByBusId(Integer busId) throws Exception {
	    if (busId == null) {
	        throw new ParamException(GET_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCreateInfoRecord> entityWrapper = new EntityWrapper<>();
	    entityWrapper.eq("del_status", UnionCreateInfoRecordConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);

		return this.selectOne(entityWrapper);
	}

}
