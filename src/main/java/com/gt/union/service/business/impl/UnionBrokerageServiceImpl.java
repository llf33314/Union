package com.gt.union.service.business.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.business.UnionBrokerageConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.entity.business.UnionBrokerage;
import com.gt.union.mapper.business.UnionBrokerageMapper;
import com.gt.union.service.business.IUnionBrokerageService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 联盟商家佣金比率 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionBrokerageServiceImpl extends ServiceImpl<UnionBrokerageMapper, UnionBrokerage> implements IUnionBrokerageService {
	private static final String UPDATE_ID_UNIONID = "UnionBrokerageServiceImpl.updateByIdAndUnionId()";
	private static final String GET_UNIONID_FROMBUSID_TOBUSID = "UnionBrokerageServiceImpl.getByUnionIdAndFromBusIdAndToBusId()";

	@Override
	public void updateByIdAndUnionId(UnionBrokerage unionBrokerage) throws Exception {
		if (CommonUtil.isEmpty(unionBrokerage.getBrokerageRatio()) || CommonUtil.isEmpty(unionBrokerage.getFromBusId()) || CommonUtil.isEmpty(unionBrokerage.getToBusId()) || CommonUtil.isEmpty(unionBrokerage.getUnionId())) {
			throw new ParamException(UPDATE_ID_UNIONID, "参数错误", ExceptionConstant.PARAM_ERROR);
		}
		//TODO 判断权限
		if (CommonUtil.isEmpty(unionBrokerage.getId())) {//新增
			unionBrokerage.setCreatetime(new Date());
			unionBrokerage.setDelStatus(0);
			unionBrokerage.setModifytime(new Date());
			this.insert(unionBrokerage);
		} else {
			unionBrokerage.setModifytime(new Date());
			updateById(unionBrokerage);
		}
	}

	@Override
	public UnionBrokerage getByUnionIdAndFromBusIdAndToBusId(Integer unionId, Integer fromBusId, Integer toBusId) throws Exception {
		if (unionId == null) {
			throw new ParamException(GET_UNIONID_FROMBUSID_TOBUSID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
		}
		if (fromBusId == null) {
		    throw new ParamException(GET_UNIONID_FROMBUSID_TOBUSID, "参数fromBusId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (toBusId == null) {
		    throw new ParamException(GET_UNIONID_FROMBUSID_TOBUSID, "参数toBusId为空", ExceptionConstant.PARAM_ERROR);
        }

        EntityWrapper entityWrapper = new EntityWrapper();
		entityWrapper.eq("del_status", UnionBrokerageConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("from_bus_id", fromBusId)
                .eq("to_bus_id", toBusId);
		return this.selectOne(entityWrapper);
	}
}
