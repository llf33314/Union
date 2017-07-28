package com.gt.union.service.card;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.card.UnionBusMemberCard;

/**
 * <p>
 * 联盟商家升级会员联盟卡 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
public interface IUnionBusMemberCardService extends IService<UnionBusMemberCard> {

	/**
	 * 根据联盟id获取联盟积分
	 * @param unionId	联盟id
	 * @return
	 */
	public double getUnionMemberIntegral(Integer unionId);
}
