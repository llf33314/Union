package com.gt.union.card.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.entity.UnionCardUpgrade;

import java.util.Date;

/**
 * <p>
 * 联盟卡升级 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionCardUpgradeService extends IService<UnionCardUpgrade> {

	UnionCardUpgrade createCardUpgrade(Integer cardId, Integer memberId, Integer cardType, Date validity, Integer payId);
}
