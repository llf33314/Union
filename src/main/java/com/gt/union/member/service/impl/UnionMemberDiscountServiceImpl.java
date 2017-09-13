package com.gt.union.member.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.member.entity.UnionMemberDiscount;
import com.gt.union.member.mapper.UnionMemberDiscountMapper;
import com.gt.union.member.service.IUnionMemberDiscountService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 盟员折扣 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMemberDiscountServiceImpl extends ServiceImpl<UnionMemberDiscountMapper, UnionMemberDiscount> implements IUnionMemberDiscountService {

	@Override
	public UnionMemberDiscount getMinDiscountByMemberList(List<Integer> members, List<Integer> memberList) {
		EntityWrapper wrapper = new EntityWrapper<UnionMemberDiscount>();
		wrapper.in("from_member_id", members.toArray());
		wrapper.in("to_member_id", memberList.toArray());
		wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		wrapper.orderBy("to_member_id", true);
		wrapper.setSqlSelect("id, DATE_FORMAT(createtime, '%Y-%m-%d %T') createtime, del_status, from_member_id, to_member_id, min(discount) as discount, DATE_FORMAT(modifytime, '%Y-%m-%d %T') modifytime ");
		return this.selectOne(wrapper);
	}
}
