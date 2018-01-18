package com.gt.union.wxapp.card.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.Member;
import com.gt.union.wxapp.card.vo.CardDetailVO;
import com.gt.union.wxapp.card.vo.MyCardDetailVO;
import com.gt.union.wxapp.card.vo.NearUserVO;

import java.util.List;

/**
 * @author hongjiye
 * @time 2017-12-29 15:06
 **/
public interface IWxAppCardService {

	/**
	 * 联盟卡粉丝端小程序首页列表数据
	 * @param phone	粉丝用户手机号
	 * @param busId	商家id
	 * @param page	分页参数
	 * @return
	 * @throws Exception
	 */
	Page listWxAppCardPage(String phone, Integer busId, Page page) throws Exception;

	/**
	 * 附近的商家列表
	 * @param busId		商家id
	 * @param enterpriseName	企业名称 模糊搜索
	 * @return
	 */
	List<NearUserVO> listNearUser(Integer busId, Integer enterpriseName);

	/**
	 * 联盟卡详情
	 *
	 * @param phone      粉丝用户手机号
	 * @param busId      商家id
	 * @param unionId    联盟id
	 * @param activityId 活动卡id
	 * @param unionMemberId
	 * @return
	 */
	CardDetailVO getCardDetail(String phone, Integer busId, Integer unionId, Integer activityId, Integer unionMemberId) throws Exception;

	/**
	 * 获取我的联盟卡详情
	 *
	 * @param phone 用户手机号
	 * @return
	 */
	MyCardDetailVO myCardDetail(String phone) throws Exception;

	/**
	 * 绑定联盟卡手机号
	 *  @param member 用户
	 * @param busId  商家id
	 * @param phone  手机号
	 * @param code   验证码
	 */
	Member bindCardPhone(Member member, Integer busId, String phone, String code) throws Exception;

	/**
	 * 办理联盟卡
	 *
	 * @param phone      手机号
	 * @param busId      商家id
	 * @param activityId 活动id
	 * @param unionId    联盟id
	 * @return
	 */
	String cardTransaction(String phone, Integer busId, Integer activityId, Integer unionId) throws Exception;

	/**
	 * 获取联盟卡消费记录
	 *
	 * @param page  分页参数
	 * @param phone 手机号
	 * @return Page
	 */
	Page pageConsumeByPhone(Page page, String phone) throws Exception;

	/**
	 * 分页获取联盟卡信息盟员列表信息
	 * @param busId        商家id
	 * @param unionId    联盟id
	 * @param activityId    活动卡id
	 * @param page        分页参数
	 * @param unionMemberId
	 * @return
	 */
	Page listCardDetailPage(Integer busId, Integer unionId, Integer activityId, Page page, Integer unionMemberId) throws Exception;

	/**
	 * 分页获取联我的联盟卡列表信息
	 * @param phone		手机号
	 * @param page		分页参数
	 * @return
	 */
	Page listMyCardPage(String phone, Page page) throws Exception;

	/**
	 * 获取支付参数
	 * @param duoFenMemberId
	 * @param orderNo
	 * @param phone
	 * @return
	 */
	String getPayParam(Integer duoFenMemberId, String orderNo, String phone) throws Exception;

}
