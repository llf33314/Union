package com.gt.union.service.card;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.card.UnionMemberCard;

/**
 * <p>
 * 联盟会员商家绑定 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-08-23
 */
public interface IUnionMemberCardService extends IService<UnionMemberCard> {

	/**
	 * 获取绑定的联盟卡
	 * @param memberId	用户id
	 * @param busId		商家id
	 * @return
	 */
	UnionMemberCard getUnionMemberCard(Integer memberId, Integer busId);
}
