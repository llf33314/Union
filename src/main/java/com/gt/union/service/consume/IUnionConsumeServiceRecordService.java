package com.gt.union.service.consume;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.api.entity.param.UnionConsumeParam;
import com.gt.union.api.entity.param.UnionRefundParam;
import com.gt.union.api.entity.result.UnionConsumeResult;
import com.gt.union.api.entity.result.UnionRefundResult;
import com.gt.union.entity.consume.UnionConsumeServiceRecord;

import java.util.List;

/**
 * <p>
 * 核销服务记录 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
public interface IUnionConsumeServiceRecordService extends IService<UnionConsumeServiceRecord> {

	/**
	 * 根据优惠服务ids查询核销过的优惠服务 根据服务id分组
	 * @param serviceIds
	 * @return
	 */
	List<UnionConsumeServiceRecord> getListGrouyByServiceId(List<Integer> serviceIds);

	/**
	 * 联盟卡核销
	 * @param unionConsumeParam
	 * @return
	 */
	UnionConsumeResult consumeByUnionCard(UnionConsumeParam unionConsumeParam);

	/**
	 * 联盟卡退款
	 * @param unionRefundParam
	 * @return
	 */
	UnionRefundResult unionRefund(UnionRefundParam unionRefundParam);
}
