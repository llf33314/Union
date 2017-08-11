package com.gt.union.service.brokerage.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.entity.brokerage.UnionBrokerageWithdrawalsRecord;
import com.gt.union.mapper.brokerage.UnionBrokerageWithdrawalsRecordMapper;
import com.gt.union.service.brokerage.IUnionBrokerageWithdrawalsRecordService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 联盟佣金提现记录 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionBrokerageWithdrawalsRecordServiceImpl extends ServiceImpl<UnionBrokerageWithdrawalsRecordMapper, UnionBrokerageWithdrawalsRecord> implements IUnionBrokerageWithdrawalsRecordService {
	private static final String GET_UNION_BROKERAGE_WITHDRAWALS_SUM = "UnionBrokerageWithdrawalsRecordServiceImpl.getUnionBrokerageWithdrawalsSum()";

	@Override
	public double getUnionBrokerageWithdrawalsSum(final Integer busId, final Integer unionId) {
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder();
				sbSqlSegment.append(" WHERE")
						.append(" bus_id = ").append(busId);
				if(CommonUtil.isNotEmpty(unionId)){
					sbSqlSegment.append(" AND union_id = ").append(unionId);
				}
				return sbSqlSegment.toString();
			};
		};
		StringBuilder sbSqlSelect = new StringBuilder();
		sbSqlSelect.append("IFNULL(sum(money),0)AS money");
		wrapper.setSqlSelect(sbSqlSelect.toString());
		Map<String,Object> data = this.selectMap(wrapper);
		return CommonUtil.toDouble(data.get("money"));
	}
}
