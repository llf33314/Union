package com.gt.union.service.business.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.entity.business.UnionRecommendPayRecord;
import com.gt.union.mapper.business.UnionRecommendPayRecordMapper;
import com.gt.union.service.business.IUnionRecommendPayRecordService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * <p>
 * 联盟商机佣金支付记录 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-27
 */
@Service
public class UnionRecommendPayRecordServiceImpl extends ServiceImpl<UnionRecommendPayRecordMapper, UnionRecommendPayRecord> implements IUnionRecommendPayRecordService {

	private Logger logger = Logger.getLogger(UnionRecommendPayRecordServiceImpl.class);

	@Autowired
	private UnionRecommendPayRecordMapper unionRecommendPayRecordMapper;

	@Override
	public double getRecommendPay(Integer busId, Integer unionId) {
		return unionRecommendPayRecordMapper.selectRecommendPaySum(busId,unionId);
	}
}
