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
	 * 根据rootId和联盟id列表查询升级的联盟卡列表
	 * @param rootId
	 * @param unionIds
	 * @return
	 */
	List<UnionCard> listByRootIdAndUnionIds(@Param("rootId") Integer rootId, @Param("unionIds") List<Integer> unionIds);

	/**
	 *
	 * @param list 联盟卡列表
	 * @param busId
	 * @param unionIds
	 * @return
	 */
	Map<String,Object> getByMinDiscountByCardList(@Param("list") List<UnionCard> list, @Param("busId") Integer busId, @Param("unionIds") List<Integer> unionIds);

	/**
	 * 根据手机号和盟员列表查询升级的联盟卡列表信息
	 * @param phone
	 * @param members
	 * @return
	 */
	List<UnionCard> listByPhoneAndMembers(@Param("phone") String phone, @Param("members") List<UnionMember> members);

	/**
	 * 查询升级联盟卡的数量
	 * @param memberList
	 * @param type
	 * @param isCharge
	 * @param isAvailable
	 * @return
	 */
	int countByMemberIdsAndType(@Param("members") List<UnionMember> memberList, @Param("type") Integer type, @Param("isCharge") Integer isCharge, @Param("phone") String phone, @Param("isAvailable")  int isAvailable);
}