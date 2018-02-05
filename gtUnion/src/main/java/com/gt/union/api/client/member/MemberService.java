package com.gt.union.api.client.member;

import com.gt.api.bean.session.Member;
import com.gt.union.common.response.GtJsonResult;

import javax.servlet.http.HttpServletRequest;
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
     * @throws Exception    统一异常处理
	 */
	boolean bindMemberPhone(Integer busId, Integer memberId, String phone) throws Exception;

	/**
	 * 小程序绑定粉丝用户手机号
	 * @param busId		商家id
	 * @param memberId	粉丝id
	 * @param phone		手机号
	 * @return	1：成功 0：失败
     * @throws Exception    统一异常处理
	 */
	Member bindMemberPhoneApp(Integer busId, Integer memberId, String phone) throws Exception;

	/**
	 * 粉丝用户手机号登录 登录成功后 member放入session中
	 * @param phone		手机号
	 * @param busId		商家id
	 * @return	1：成功 0：失败
     * @throws Exception    统一异常处理
	 */
	boolean loginMemberByPhone(String phone, Integer busId) throws Exception;

	/**
	 * uc登录或微信授权登录
	 * @param request
	 * @param busId		登录授权的商家id
	 * @param ucLogin	是否可以在uc登录  false：不能在uc登录 true：可以在uc登录
	 * @param reqUrl	请求的url，即授权登录后重定向的链接
	 * @param ucLoginUrl	自定义uc登录的地址 如果没有自定义的，可以不填（默认会员登录地址）
	 * @return
	 * @throws Exception
	 */
	GtJsonResult authorizeMember(HttpServletRequest request, Integer busId, boolean ucLogin, String reqUrl, String ucLoginUrl);

	/**
	 * 微信授权（多粉账号）登录
	 * @param request
	 * @param reqUrl	授权后重定向的地址
	 * @return
	 * @throws Exception
	 */
	GtJsonResult authorizeMemberWx(HttpServletRequest request, String reqUrl);
}
