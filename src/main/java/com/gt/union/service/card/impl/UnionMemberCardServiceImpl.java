package com.gt.union.service.card.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import com.gt.union.entity.card.UnionMemberCard;
import com.gt.union.mapper.card.UnionMemberCardMapper;
import com.gt.union.service.card.IUnionMemberCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 联盟会员商家绑定 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-08-23
 */
@Service
public class UnionMemberCardServiceImpl extends ServiceImpl<UnionMemberCardMapper, UnionMemberCard> implements IUnionMemberCardService {

	@Autowired
	private RedisCacheUtil redisCacheUtil;


	@Override
	public UnionMemberCard getUnionMemberCard(Integer memberId, Integer busId) {
		String memberCardKey = RedisKeyUtil.getUnionMemberCardKey(memberId,busId);
		if(redisCacheUtil.exists(memberCardKey)){
			Object obj = redisCacheUtil.get(memberCardKey);
			if(CommonUtil.isNotEmpty(obj)){
				return JSON.parseObject(obj.toString(), UnionMemberCard.class);
			}
		}
		EntityWrapper entityWrapper = new EntityWrapper<UnionMemberCard>();
		entityWrapper.eq("del_status", 0 );
		entityWrapper.eq("member_id",memberId);
		entityWrapper.eq("bus_id",busId);
		UnionMemberCard card = this.selectOne(entityWrapper);
		if(CommonUtil.isNotEmpty(card)){
			redisCacheUtil.set(memberCardKey,JSON.toJSONString(card));
		}
		return card;
	}
}
