package com.gt.union.mapper.card;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.gt.union.entity.card.UnionBusMemberCard;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
  * 联盟商家升级会员联盟卡 Mapper 接口
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
public interface UnionBusMemberCardMapper extends BaseMapper<UnionBusMemberCard> {

	/**
	 * 根据联盟id获取联盟积分
	 * @param unionId 联盟id
	 * @return
	 */
	double getUnionMemberIntegral(@Param("unionId") Integer unionId);
}