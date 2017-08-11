package com.gt.union.service.business.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
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
	private static final String UPDATE_UNION_BROKERAGE = "UnionBrokerageServiceImpl.updateUnionBrokerage()";

	@Override
	public void updateUnionBrokerage(UnionBrokerage unionBrokerage) throws Exception {
		if (CommonUtil.isEmpty(unionBrokerage.getBrokerageRatio()) || CommonUtil.isEmpty(unionBrokerage.getFromBusId()) || CommonUtil.isEmpty(unionBrokerage.getToBusId()) || CommonUtil.isEmpty(unionBrokerage.getUnionId())) {
			throw new ParamException(UPDATE_UNION_BROKERAGE, "参数错误", ExceptionConstant.PARAM_ERROR);
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
}
