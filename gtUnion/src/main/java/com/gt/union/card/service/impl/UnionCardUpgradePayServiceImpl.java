package com.gt.union.card.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionCardUpgradePay;
import com.gt.union.card.mapper.UnionCardUpgradePayMapper;
import com.gt.union.card.service.IUnionCardUpgradePayService;
import com.gt.union.common.constant.CommonConstant;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 购买升级联盟卡记录 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionCardUpgradePayServiceImpl extends ServiceImpl<UnionCardUpgradePayMapper, UnionCardUpgradePay> implements IUnionCardUpgradePayService {

	@Override
	public UnionCardUpgradePay createCardUpgreadePay(String orderNo, int payStatus, int payType, Double payMoney, String orderDesc) {
		UnionCardUpgradePay pay = new UnionCardUpgradePay();
		pay.setCreatetime(new Date());
		pay.setDelStatus(CommonConstant.DEL_STATUS_NO);
		pay.setOrderDesc(orderDesc);
		pay.setOrderNo(orderNo);
		pay.setPayMoney(payMoney);
		pay.setPayStatus(payStatus);
		pay.setPayType(payType);
		this.insert(pay);
		return pay;
	}
}
