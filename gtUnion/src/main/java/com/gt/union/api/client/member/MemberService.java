package com.gt.union.api.client.member;

import com.gt.api.bean.session.Member;

import java.util.List;
import java.util.Map;

/**
 *
 * 粉丝会员接口服务
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0018.
 */
public interface MemberService {

	/**
	 * 根据商家id和用户ids查询列表信息
	 * @param busId		商家id
	 * @param memberIds	用户ids
	 * @return
	 */
	List<Map> listByBusIdAndMemberIds(Integer busId, String memberIds);

	/**
	 * 根据粉丝用户id获取用户信息
	 * @param memberId		粉丝用户id
	 * @return
	 */
	Member getById(Integer memberId);

	/**
	 * 根据手机号和商家id获取用户信息
	 * @param phone		手机号
	 * @param busId		商家id
	 * @return
	 */
	Member getByPhoneAndBusId(String phone, Integer busId);

	/**
	 * 绑定粉丝用户手机号
	 * @param busId		商家id
	 * @param memberId	粉丝id
	 * @param phone		手机号
	 * @return	1：成功 0：失败
	 */
	boolean bindMemberPhone(Integer busId, Integer memberId, String phone);

	/**
	 * 粉丝用户手机号登录
	 * @param phone		手机号
	 * @param busId		商家id
	 * @return	1：成功 0：失败
	 */
	boolean loginMemberByPhone(String phone, Integer busId);
}
