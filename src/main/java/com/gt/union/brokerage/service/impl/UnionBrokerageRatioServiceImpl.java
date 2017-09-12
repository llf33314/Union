package com.gt.union.brokerage.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.brokerage.entity.UnionBrokerageRatio;
import com.gt.union.brokerage.mapper.UnionBrokerageRatioMapper;
import com.gt.union.brokerage.service.IUnionBrokerageRatioService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商机佣金比率 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionBrokerageRatioServiceImpl extends ServiceImpl<UnionBrokerageRatioMapper, UnionBrokerageRatio> implements IUnionBrokerageRatioService {

	@Override
	public UnionBrokerageRatio getByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId) throws Exception{
		if(fromMemberId == null || toMemberId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.eq("", fromMemberId);
		wrapper.eq("", toMemberId);
		return null;
	}
}
