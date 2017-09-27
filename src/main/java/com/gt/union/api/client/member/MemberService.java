package com.gt.union.api.client.member;

import com.gt.api.bean.session.Member;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
public interface MemberService {

	/**
	 * 根据商家id和用户ids查询列表信息
	 * @param busId
	 * @param memberIds
	 * @return
	 */
	List<Map> listByBusIdAndMemberIds(Integer busId, String memberIds);

	/**
	 * 根据粉丝用户id获取用户信息
	 * @param memberId
	 * @return
	 */
	Member getById(Integer memberId);

	/**
	 * 根据手机号和商家id获取用户信息
	 * @param phone
	 * @param busId
	 * @return
	 */
	Member findByPhoneAndBusId(String phone, Integer busId);

	/**
	 * 绑定会员手机号
	 * @param busId
	 * @param memberId
	 * @param phone
	 * @return
	 */
	int bindMemberPhone(Integer busId, Integer memberId, String phone);
}
