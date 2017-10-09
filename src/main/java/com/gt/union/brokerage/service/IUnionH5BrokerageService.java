package com.gt.union.brokerage.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.opportunity.entity.UnionOpportunity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
public interface IUnionH5BrokerageService {

	/**
	 * 佣金平台手机端验证登录
	 * @param type        登录类型 1：商家账号 2：手机
	 * @param username    用户名
	 * @param userpwd    用户密码
	 * @param phone        手机号
	 * @param code        验证码
	 * @param request
	 */
	void checkLogin(Integer type, String username, String userpwd, String phone, String code, HttpServletRequest request) throws Exception;

	/**
	 * 获取我获得的总佣金总和（已支付的）
	 * @param busId
	 * @return
	 */
	double getSumInComeUnionBrokerage(Integer busId);

	/**
	 * 获取我已提现的佣金总和
	 * @param busId
	 * @return
	 */
	double getSumWithdrawalsUnionBrokerage(Integer busId);

	/**
	 * 根据佣金所得类型获取该类型佣金总和
	 * @param busId
	 * @param type
	 * @return
	 */
	double getSumInComeUnionBrokerageByType(Integer busId, int type);

	/**
	 * 商家id
	 * @param page
	 * @param busId
	 * @return
	 */
	Page listWithdrawals(Page page, Integer busId);

	/**
	 * 根据商家id获取未收入的佣金总和
	 * @param busId
	 * @param unionId
	 * @return
	 */
	double getUnComeUnionBrokerage(Integer busId, Integer unionId) throws Exception;

	/**
	 * 根据商家id获取未收入的佣金列表
	 * @param page
	 * @param busId
	 * @param unionId
	 * @return
	 */
	Page listUnComeUnionBrokerage(Page page, Integer busId, Integer unionId) throws Exception;

	/**
	 * 根据联盟id和商家id获取未支付给别人佣金之和   unionId可以为空
	 * @param unionId
	 * @param busId
	 * @return
	 */
	double getSumUnPayUnionBrokerage(Integer unionId, Integer busId) throws Exception;

	/**
	 * 根据联盟id和商家id获取未支付给别人佣金列表   unionId可以为空
	 * @param page
	 * @param busId
	 * @param unionId
	 * @return
	 */
	Page listUnPayUnionBrokerage(Page page, Integer busId, Integer unionId) throws Exception;

	/**
	 * 根据联盟id和商家id获取已支付给别人佣金列表   unionId可以为空
	 * @param page
	 * @param busId
	 * @param unionId
	 * @return
	 */
	Page listPayUnionBrokerage(Page page, Integer busId, Integer unionId) throws Exception;

	/**
	 * 根据联盟id和商家id获取已支付给别人佣金之和   unionId可以为空
	 * @param unionId
	 * @param busId
	 * @return
	 */
	double getSumPayUnionBrokerage(Integer unionId, Integer busId) throws Exception;

	/**
	 * 已支付给我的佣金明细列表
	 * @param page
	 * @param busId
	 * @param unionId
	 * @return
	 */
	Page listOpportunityPayToMe(Page page, Integer busId, Integer unionId) throws Exception;

	/**
	 * 已支付给我的佣金明细之和
	 * @param busId
	 * @param unionId
	 * @return
	 */
	double getOpportunitySumToMe(Integer busId, Integer unionId) throws Exception;

	/**
	 * 我获取的付费的售卡佣金列表
	 * @param page
	 * @param busId
	 * @param unionId
	 * @return
	 */
	Page listCardDivide(Page page, Integer busId, Integer unionId) throws Exception;

	/**
	 * 我获取的付费的售卡佣金之和
	 * @param busId
	 * @param unionId
	 * @return
	 */
	double getCardDivideSum(Integer busId, Integer unionId) throws Exception;

	/**
	 * 催促佣金
	 * @param busId
	 * @param id
	 */
	void urgeOpportunity(Integer busId, Integer id) throws Exception;

	/**
	 * 支付所有佣金
	 * @param busId
	 * @param unionId
	 */
	double getPayAllOpportunitySum(Integer busId, Integer unionId) throws Exception;

	/**
	 * 返回支付的地址
	 * @param busId
	 * @param unionId
	 * @param fee
	 * @param url
	 * @param memberId
	 * @return
	 */
	String payAllOpportunity(Integer busId, Integer unionId, Double fee, String url, Integer memberId) throws Exception;

	/**
	 * 支付单个佣金
	 * @param id
	 * @param url
	 * @param memberId
	 * @param busId
	 * @return
	 */
	String payOpportunity(Integer id, String url, Integer memberId, Integer busId) throws Exception;

	/**
	 * 单个商机佣金支付成功回调
	 *  @param id,
	 * @param orderNo
	 * @param verifierId
	 */
	void paymentOneOpportunitySuccess(Integer id, String orderNo, Integer verifierId) throws Exception;

	/**
	 * 多个商机佣金支付成功回调
	 * @param busId
	 * @param unionId
	 * @param orderNo
	 * @param verifierId
	 * @throws Exception
	 */
	void payAllOpportunitySuccess(Integer busId, Integer unionId, String orderNo, Integer verifierId) throws Exception;

	/**
	 * 查询未支付的佣金列表
	 * @param busId
	 * @param unionId
	 * @return
	 */
	List<UnionOpportunity> listAllUnPayUnionBrokerage(Integer busId, Integer unionId) throws Exception;
}
