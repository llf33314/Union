package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionTransferRecordConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.entity.basic.UnionCreateInfoRecord;
import com.gt.union.entity.basic.UnionEstablishRecord;
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
    private static final String EXIST_UNIONID_BUSID_CONFIRMSTATUS = "UnionTransferRecordServiceImpl.existByUnionIdAndBusIdAndConfirmStatus()";
    private static final String SAVE = "UnionTransferRecordServiceImpl.save()";

	@Override
	public UnionTransferRecord get(Integer unionId, Integer busId) {
		EntityWrapper entityWrapper = new EntityWrapper<UnionTransferRecord>();
		entityWrapper.eq("union_id",unionId);
		entityWrapper.eq("to_bus_id",busId);
		entityWrapper.eq("del_status",UnionTransferRecordConstant.DEL_STATUS_NO);
		entityWrapper.eq("no_advice",UnionTransferRecordConstant.NO_ADVICE_NO);
		entityWrapper.eq("confirm_status",UnionTransferRecordConstant.CONFIRM_STATUS_UNCHECK);
		return this.selectOne(entityWrapper);
	}

	@Override
	public boolean existByUnionIdAndBusIdAndConfirmStatus(Integer unionId, Integer toBusId, Integer confirmStatus) throws Exception {
		if (unionId == null) {
		    throw new ParamException(EXIST_UNIONID_BUSID_CONFIRMSTATUS, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (toBusId == null) {
		    throw new ParamException(EXIST_UNIONID_BUSID_CONFIRMSTATUS, "参数toBusId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (confirmStatus == null) {
		    throw new ParamException(EXIST_UNIONID_BUSID_CONFIRMSTATUS, "参数confirmStatus为空", ExceptionConstant.PARAM_ERROR);
        }

        EntityWrapper entityWrapper = new EntityWrapper();
		entityWrapper.eq("del_status", UnionTransferRecordConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("to_bus_id", toBusId)
                .eq("confirm_status", confirmStatus);
		return this.selectCount(entityWrapper) > 0 ? true : false;
	}

    @Override
    public void save(UnionTransferRecord unionTransferRecord) throws Exception {
        if (unionTransferRecord == null) {
            throw new ParamException(SAVE, "参数unionTransferRecord为空", ExceptionConstant.PARAM_ERROR);
        }

        this.save(unionTransferRecord);
    }

	@Override
	public void updateBatch(final Integer unionId) {
		Wrapper wrapper = new Wrapper(){
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder("");
				sbSqlSegment.append("set del_status = ").append(UnionTransferRecordConstant.DEL_STATUS_YES)
						.append(" where union_id = ").append(unionId);
				return sbSqlSegment.toString();
			}
		};
		UnionTransferRecord record = new UnionTransferRecord();
		this.update(record,wrapper);
	}
}
