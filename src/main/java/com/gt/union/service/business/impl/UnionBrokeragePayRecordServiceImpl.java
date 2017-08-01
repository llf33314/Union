package com.gt.union.service.business.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.entity.business.UnionBrokeragePayRecord;
import com.gt.union.mapper.business.UnionBrokeragePayRecordMapper;
import com.gt.union.service.business.IUnionBrokeragePayRecordService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 佣金支付到平台记录 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionBrokeragePayRecordServiceImpl extends ServiceImpl<UnionBrokeragePayRecordMapper, UnionBrokeragePayRecord> implements IUnionBrokeragePayRecordService {

	@Override
	public double getBrokeragePayRecordSum(final Integer busId,final Integer unionId) {
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder();
				sbSqlSegment.append(" WHERE")
						.append(" obtain_bus_id = ").append(busId)
						.append(" AND status = ").append(2);
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
