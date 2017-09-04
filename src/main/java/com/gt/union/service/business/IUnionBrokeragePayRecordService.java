package com.gt.union.service.business;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.business.UnionBrokeragePayRecord;
import com.gt.union.entity.business.UnionBusinessRecommend;

import java.util.List;

/**
 * <p>
 * 佣金支付到平台记录 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
public interface IUnionBrokeragePayRecordService extends IService<UnionBrokeragePayRecord> {

	/**
	 * 查询商机佣金支付总和  可根据联盟id查
	 * @param busId	商家id
	 * @param unionId	联盟id
	 * @return
	 */
	public double getBrokeragePayRecordSum(Integer busId, Integer unionId);


	/**
	 * 根据推荐商家列表添加佣金支付记录
	 * @param list
	 * @param orderNo
	 */
	void insertBatchByRecommends(List<UnionBusinessRecommend> list, String orderNo);
}
