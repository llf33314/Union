package com.gt.union.brokerage.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.brokerage.mapper.UnionBrokerageWithdrawalMapper;
import com.gt.union.brokerage.service.IUnionBrokerageWithdrawalService;
import com.gt.union.member.entity.UnionMember;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 佣金提现记录 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionBrokerageWithdrawalServiceImpl extends ServiceImpl<UnionBrokerageWithdrawalMapper, UnionBrokerageWithdrawal> implements IUnionBrokerageWithdrawalService {

	@Override
	public List<UnionBrokerageWithdrawal> listWithdrawalByMemberIds(List<UnionMember> members) {
		List<Integer> ids = new ArrayList<Integer>();
		for(UnionMember member : members){
			ids.add(member.getId());
		}
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.in("member_id", ids.toArray());
		wrapper.groupBy("member_id");
		wrapper.setSqlSelect("IFNULL(SUM(money), 0) money, member_id");
		return this.selectList(wrapper);
	}

	@Override
	public Double withdrawalSumByMemberIds(List<UnionMember> members) {
		List<Integer> ids = new ArrayList<Integer>();
		for(UnionMember member : members){
			ids.add(member.getId());
		}
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.in("member_id", ids.toArray());
		wrapper.setSqlSelect("IFNULL(SUM(money), 0) money");
		Map<String,Object> data = this.selectMap(wrapper);
		if(data == null){
			return 0d;
		}
		return Double.valueOf(data.get("money").toString());
	}
}
