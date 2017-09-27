package com.gt.union.brokerage.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.brokerage.mapper.UnionBrokerageWithdrawalMapper;
import com.gt.union.brokerage.service.IUnionBrokerageWithdrawalService;
import com.gt.union.brokerage.vo.UnionBrokerageWithDrawalsVO;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.member.entity.UnionMember;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private UnionBrokerageWithdrawalMapper unionBrokerageWithdrawalMapper;

	@Autowired
	private MemberService memberService;

	@Override
	public List<UnionBrokerageWithdrawal> listWithdrawalByMemberIds(List<UnionMember> members) {
		List<Integer> ids = new ArrayList<Integer>();
		for(UnionMember member : members){
			ids.add(member.getId());
		}
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.eq("del_status",CommonConstant.DEL_STATUS_NO);
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
		wrapper.eq("del_status",CommonConstant.DEL_STATUS_NO);
		wrapper.setSqlSelect("IFNULL(SUM(money), 0) money");
		Map<String,Object> data = this.selectMap(wrapper);
		if(data == null){
			return 0d;
		}
		return Double.valueOf(data.get("money").toString());
	}

	@Override
	public double getSumWithdrawalsUnionBrokerage(Integer busId) {
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.eq("bus_id",busId);
		wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		wrapper.setSqlSelect("IFNULL(SUM(money), 0) money");
		Map<String,Object> data = this.selectMap(wrapper);
		if(data == null){
			return 0;
		}
		return Double.valueOf(data.get("money").toString());
	}

	@Override
	public Page listWithdrawals(Page page, Integer busId) {
		List<UnionBrokerageWithDrawalsVO> list = unionBrokerageWithdrawalMapper.listWithdrawals(page,busId);
		String ids = "";
		int size = 1;
		for(UnionBrokerageWithDrawalsVO vo : list){
			if(size == list.size()){
				if(CommonUtil.isNotEmpty(vo.getMemberId())){
					ids += vo.getMemberId();
				}
			}else {
				if(CommonUtil.isNotEmpty(vo.getMemberId())){
					ids += vo.getMemberId() + ",";
				}
			}
			size++;
		}
		if(StringUtil.isNotEmpty(ids)){
			List<Map> members = memberService.listByBusIdAndMemberIds(busId,ids);
			if(ListUtil.isNotEmpty(members)){
				for(UnionBrokerageWithDrawalsVO vo : list){
					for(Map map : members){
						if(CommonUtil.isNotEmpty(vo.getMemberId())){
							if(vo.getMemberId().equals(map.get("id"))){
								vo.setNickName(CommonUtil.isEmpty(map.get("nickname")) ? "未知用户" : map.get("nickname").toString());
								break;
							}
						}else {
							break;
						}
					}
				}
			}
		}
		page.setRecords(list);
		return page;
	}

}
