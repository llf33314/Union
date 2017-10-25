package com.gt.union.card.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.entity.UnionCardUpgradePay;

/**
 * <p>
 * 购买升级联盟卡记录 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionCardUpgradePayService extends IService<UnionCardUpgradePay> {

	/**
	 * 添加支付购买联盟卡记录信息
	 * @param orderNo	订单号
	 * @param payStatus	支付状态
	 * @param payType
	 * @param payMoney
	 * @param orderDesc
	 * @return
	 */
	UnionCardUpgradePay createCardUpgreadePay(String orderNo, int payStatus, int payType, Double payMoney, String orderDesc);
}
