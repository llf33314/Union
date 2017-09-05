package com.gt.union.service.business.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.brokerage.UnionIncomeExpenseRecordConstant;
import com.gt.union.common.constant.business.UnionBrokeragePayRecordConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.entity.brokerage.UnionIncomeExpenseRecord;
import com.gt.union.entity.business.UnionBrokeragePayRecord;
import com.gt.union.entity.business.UnionBusinessRecommend;
import com.gt.union.entity.business.UnionPayRecommend;
import com.gt.union.mapper.business.UnionBrokeragePayRecordMapper;
import com.gt.union.service.brokerage.IUnionIncomeExpenseRecordService;
import com.gt.union.service.business.IUnionBrokeragePayRecordService;
import com.gt.union.service.business.IUnionPayRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

	@Autowired
	private IUnionIncomeExpenseRecordService unionIncomeExpenseRecordService;

	@Autowired
	private IUnionPayRecommendService unionPayRecommendService;

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

	@Override
	public void insertBatchByRecommends(List<UnionBusinessRecommend> list, String orderNo) {
		List<UnionBrokeragePayRecord> records = new ArrayList<UnionBrokeragePayRecord>();
		List<UnionIncomeExpenseRecord> incomeExpenseRecords = new ArrayList<UnionIncomeExpenseRecord>();
		List<UnionPayRecommend> payRecommends = new ArrayList<UnionPayRecommend>();
		for(UnionBusinessRecommend recommend : list){
			//添加佣金支付记录
			UnionBrokeragePayRecord record = new UnionBrokeragePayRecord();
			record.setCreatetime(new Date());
			record.setDelStatus(UnionBrokeragePayRecordConstant.DEL_STATUS_NO);
			record.setOrderNo(orderNo);
			record.setUnionId(recommend.getUnionId());
			record.setPayBusId(recommend.getToBusId());
			record.setObtainBusId(recommend.getFromBusId());
			record.setStatus(UnionBrokeragePayRecordConstant.PAY_STATUS_SUCCESS);
			record.setType(UnionBrokeragePayRecordConstant.PAY_TYPE_WX);
			record.setMoney(recommend.getBusinessPrice());
			records.add(record);

			//添加收入支出记录
			UnionIncomeExpenseRecord unionIncomeExpenseRecord = new UnionIncomeExpenseRecord();
			unionIncomeExpenseRecord.setMoney(recommend.getBusinessPrice());
			unionIncomeExpenseRecord.setCreatetime(new Date());
			unionIncomeExpenseRecord.setSource(UnionIncomeExpenseRecordConstant.SOURCE_BUSINESS_RECOMMEND);
			unionIncomeExpenseRecord.setType(UnionIncomeExpenseRecordConstant.TYPE_INCOME);
			unionIncomeExpenseRecord.setUnionId(recommend.getUnionId());
			unionIncomeExpenseRecord.setBusId(recommend.getFromBusId());
			unionIncomeExpenseRecord.setBusinessRecommendId(recommend.getId());
			incomeExpenseRecords.add(unionIncomeExpenseRecord);

			//添加商机佣金支付管理记录
			UnionPayRecommend unionPayRecommend = new UnionPayRecommend();
			unionPayRecommend.setRecommendId(recommend.getId());
			payRecommends.add(unionPayRecommend);
		}
		this.insertBatch(records);

		unionIncomeExpenseRecordService.insertBatch(incomeExpenseRecords);

		for(int i = 0; i < records.size(); i++){
			payRecommends.get(i).setPayRecordId(records.get(i).getId());
		}
		unionPayRecommendService.insertBatch(payRecommends);
	}
}
