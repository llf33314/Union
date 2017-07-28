package com.gt.union.service.business;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.business.UnionRecommendPayRecord;

/**
 * <p>
 * 联盟商机佣金支付记录 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-27
 */
public interface IUnionRecommendPayRecordService extends IService<UnionRecommendPayRecord> {

	/**
	 * 获取联盟内的商家的佣金总和
	 * @param busId  主账号商家id
	 * @param unionId	联盟id
	 * @return
	 */
	public double getRecommendPay(Integer busId, Integer unionId);

	/**
	 * 查询
	 * @param busId
	 * @param unionId
	 * @param delStatus
	 * @return
	 */
	//public double getAllRecommendPay(Integer busId, Integer unionId, Integer delStatus);
}
