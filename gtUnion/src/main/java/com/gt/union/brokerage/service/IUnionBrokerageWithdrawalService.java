package com.gt.union.brokerage.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.member.entity.UnionMember;

import java.util.List;

/**
 * <p>
 * 佣金提现记录 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionBrokerageWithdrawalService extends IService<UnionBrokerageWithdrawal> {

	/**
	 * 根据盟员列表查询支付的提现的金额列表
	 * @param members
	 * @return
	 */
	List<UnionBrokerageWithdrawal> listWithdrawalByMemberIds(List<UnionMember> members);

	/**
	 * 根据盟员id列表查询总提现金额
	 * @param members
	 * @return
	 */
	Double withdrawalSumByMemberIds(List<UnionMember> members);

	/**
	 * 获取我已提现的佣金总和
	 * @param busId
	 * @return
	 */
	double getSumWithdrawalsUnionBrokerage(Integer busId);

	/**
	 * 获取提现记录列表
	 * @param page
	 * @param busId
	 * @return
	 */
	Page listWithdrawals(Page page, Integer busId);

}
