package com.gt.union.card.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
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
	int countByMemberIdsAndType(@Param("members") List<Integer> memberList, @Param("type") Integer type, @Param("isCharge") Integer isCharge, @Param("phone") String phone);

	/**
	 * 根据联盟卡列表查询最早办理的联盟卡
	 * @param list
	 * @return
	 */
	Map<String,Object> getByEarliestByCardList(@Param("list") List<UnionCard> list);

	/**
	 * 查询数量 判断该联盟卡在该联盟卡是否升级了该联盟卡类型
	 * @param cardType	联盟卡类型 1：黑卡 2：红卡
	 * @param rootId	联盟卡主id
	 * @param unionId	联盟id
	 * @return
	 */
	int countCardByTypeAndRootIdAndUnionId(@Param("cardType")int cardType, @Param("rootId")Integer rootId, @Param("unionId")Integer unionId);
}