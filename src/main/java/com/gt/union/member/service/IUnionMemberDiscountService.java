package com.gt.union.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.member.entity.UnionMemberDiscount;

import java.util.List;

/**
 * <p>
 * 盟员折扣 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMemberDiscountService extends IService<UnionMemberDiscount> {

	/**
	 * 查询最低折扣
	 * @param members   我的盟员身份列表
	 * @param memberList    和我有盟员关系的其他盟员列表
	 * @return
	 */
	UnionMemberDiscount getMinDiscountByMemberList(List<Integer> members, List<Integer> memberList);
}
