package com.gt.union.card.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.gt.union.card.entity.UnionCard;
import com.gt.union.member.entity.UnionMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
  * 联盟卡信息 Mapper 接口
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface UnionCardMapper extends BaseMapper<UnionCard> {

	/**
	 * 获取该盟员（memberId）给这些联盟卡（list）的盟员设置最低的折扣
	 * @param list 联盟卡列表
	 * @param memberId
	 * @return
	 */
	Map<String,Object> getByMinDiscountByCardList(@Param("list") List<UnionCard> list, @Param("memberId") Integer memberId);

	/**
	 * 查询升级联盟卡的数量
	 * @param memberList
	 * @param type
	 * @param isCharge
	 * @return
	 */
	int countByMemberIdsAndType(@Param("members") List<Integer> memberList, @Param("type") Integer type, @Param("isCharge") Integer isCharge, @Param("rootId") Integer rootId);

	/**
	 * 根据联盟卡列表查询最早办理的联盟卡
	 * @param list
	 * @return
	 */
	Map<String,Object> getByEarliestByCardList(@Param("list") List<UnionCard> list);
}