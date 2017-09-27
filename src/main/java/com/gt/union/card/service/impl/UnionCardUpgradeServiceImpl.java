package com.gt.union.card.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionCardUpgrade;
import com.gt.union.card.mapper.UnionCardUpgradeMapper;
import com.gt.union.card.service.IUnionCardUpgradeService;
import com.gt.union.common.constant.CommonConstant;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 联盟卡升级 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionCardUpgradeServiceImpl extends ServiceImpl<UnionCardUpgradeMapper, UnionCardUpgrade> implements IUnionCardUpgradeService {

	@Override
	public UnionCardUpgrade createCardUpgrade(Integer cardId, Integer memberId, Integer cardType, Date validity, Integer payId) {
		UnionCardUpgrade upgrade = new UnionCardUpgrade();
		upgrade.setCreatetime(new Date());
		upgrade.setDelStatus(CommonConstant.DEL_STATUS_NO);
		upgrade.setCardId(cardId);
		upgrade.setMemberId(memberId);
		upgrade.setType(cardType);
		upgrade.setValidity(validity);
		upgrade.setUpgradePayId(payId);
		this.insert(upgrade);
		return upgrade;
	}
}
