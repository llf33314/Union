package com.gt.union.brokerage.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.gt.union.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.member.entity.UnionMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
  * 佣金收入 Mapper 接口
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface UnionBrokerageIncomeMapper extends BaseMapper<UnionBrokerageIncome> {

	/**
	 * 根据盟员列表ids查询收入佣金金额
	 * @param members
	 * @return
	 */
	List<Map<String,Object>> listIncomeByMemberIds(@Param("members") List<UnionMember> members);
}