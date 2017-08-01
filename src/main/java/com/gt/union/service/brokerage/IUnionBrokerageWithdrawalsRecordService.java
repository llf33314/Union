package com.gt.union.service.brokerage;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.brokerage.UnionBrokerageWithdrawalsRecord;

/**
 * <p>
 * 联盟佣金提现记录 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
public interface IUnionBrokerageWithdrawalsRecordService extends IService<UnionBrokerageWithdrawalsRecord> {

	/**
	 * 查询佣金提现记录  包括推荐佣金和售卡佣金
	 * @param busId	商家id
	 * @param unionId	联盟id 可以为空
	 * @return
	 */
	public double getUnionBrokerageWithdrawalsSum(Integer busId, Integer unionId);
}
