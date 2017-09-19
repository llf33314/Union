package com.gt.union.brokerage.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.member.entity.UnionMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
public interface UnionH5BrokerageMapper extends BaseMapper<UnionBrokerageWithdrawal> {
	/**
	 * 获取商家未收佣金之和
	 * @param members
	 * @return
	 */
	double getUnComeUnionBrokerage(@Param("members") List<UnionMember> members);

	/**
	 * 根据联盟id和商家id获取未支付给别人佣金之和   unionId可以为空
	 * @param members
	 * @return
	 */
	double getSumUnPayUnionBrokerage(@Param("members") List<UnionMember> members);

	/**
	 * 根据联盟id和商家id获取未支付给别人佣金列表   unionId可以为空
	 * @param page
	 * @param members
	 * @return
	 */
	List<Map<String,Object>> listUnPayUnionBrokerage(Page page, @Param("members") List<UnionMember> members);

	/**
	 * 根据联盟id和商家id获取已支付给别人佣金列表   unionId可以为空
	 * @param page
	 * @param members
	 * @return
	 */
	List<Map<String,Object>> listPayUnionBrokerage(Page page, @Param("members") List<UnionMember> members);

	/**
	 * 根据联盟id和商家id获取已支付给别人佣金之和   unionId可以为空
	 * @param members
	 * @return
	 */
	double getSumPayUnionBrokerage(@Param("members") List<UnionMember> members);

	/**
	 * 根据商家id获取未收入的佣金列表
	 * @param page
	 * @param members
	 * @return
	 */
	List<Map<String,Object>> listUnComeUnionBrokerage(Page page, @Param("members") List<UnionMember> members);

	/**
	 * 已支付给我的佣金明细列表
	 * @param page
	 * @param members
	 * @return
	 */
	List<Map<String,Object>> listOpportunityPayToMe(Page page, @Param("members") List<UnionMember> members);

	/**
	 * 已支付给我的佣金明细之和
	 * @param members
	 * @return
	 */
	double getOpportunitySumToMe(@Param("members") List<UnionMember> members);

	/**
	 * 我获取的付费的售卡佣金列表
	 * @param page
	 * @param members
	 * @param busId
	 * @return
	 */
	List<Map<String,Object>> listCardDivide(Page page, @Param("members") List<UnionMember> members, @Param("busId") Integer busId);

	/**
	 * 我获取的付费的售卡佣金之和
	 * @param members
	 * @param busId
	 * @return
	 */
	double getCardDivideSum(@Param("members") List<UnionMember> members, @Param("busId") Integer busId);
}
