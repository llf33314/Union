package com.gt.union.opportunity.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.opportunity.entity.UnionOpportunity;
import com.gt.union.opportunity.vo.UnionOpportunityVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商机推荐 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionOpportunityService extends IService<UnionOpportunity> {

	/**
	 * 查询推荐给我的商机
	 * @param page
	 * @param busId		商家id
	 * @param unionId	联盟id
	 * @param isAccept	处理状态
	 * @param clientName	客户名称
	 * @param clientPhone	客户电话
	 * @return
	 */
	Page listToMy(Page page, Integer busId, Integer unionId, String isAccept, String clientName, String clientPhone) throws Exception;

	/**
	 * 查询我推荐的商机
	 * @param page
	 * @param busId		商家id
	 * @param unionId	联盟id
	 * @param isAccept	处理状态
	 * @param clientName	客户名称
	 * @param clientPhone	客户电话
	 * @return
	 */
	Page listFromMy(Page page, Integer busId, Integer unionId, String isAccept, String clientName, String clientPhone) throws Exception;

	/**
	 * 审核商机
	 * @param busId	商家id
	 * @param id	商机id
	 * @param isAccept	处理状态
	 * @param acceptPrice	受理价格
	 */
	void updateByIdAndIsAccept(Integer busId, Integer id, Integer isAccept, Double acceptPrice) throws Exception;

	/**
	 * 商机推荐
	 * @param vo
	 */
	void save(UnionOpportunityVO vo) throws Exception;

	/**
	 *	二维码支付佣金  生成支付信息
	 * @param busId
	 * @param ids
	 * @return
	 */
	Map<String,Object> payOpportunityQRCode(Integer busId, String ids) throws Exception;

	/**
	 * 佣金扫码支付成功后回调
	 * @param encrypt
	 * @param only
	 */
	void payOpportunitySuccess(String encrypt, String only) throws Exception;

	/**
	 * 查询我推荐的已处理的商机列表
	 * @param page
	 * @param fromBusId	本商家的id
	 * @param toMemberId	被推荐的盟员id
	 * @param unionId		联盟id
	 * @param status	是否结算  1：已结算 2：未结算
	 * @param clientName	客户名称
	 * @param clientPhone	客户电话
	 * @return
	 */
	Page listBrokerageFromMy(Page page, Integer fromBusId, Integer toMemberId, Integer unionId, String status, String clientName, String clientPhone) throws Exception;

	/**
	 * 查询推荐给我的已处理的商机列表
	 * @param page
	 * @param toBusId	本商家的id
	 * @param fromMemberId	推荐的盟员id
	 * @param unionId		联盟id
	 * @param status	是否结算  1：已结算 2：未结算
	 * @param clientName	客户名称
	 * @param clientPhone	客户电话
	 * @return
	 */
	Page listBrokerageToMy(Page page, Integer toBusId, Integer fromMemberId, Integer unionId, String status, String clientName, String clientPhone) throws Exception;

	/**
	 * 查询我的商机佣金支付明细
	 * @param page
	 * @param busId
	 * @param unionId
	 * @return
	 */
	Page listPayDetail(Page page, Integer busId, Integer unionId) throws Exception;

	/**
	 * 查询在联盟下我与某盟员的佣金来往明细
	 * @param memberId    盟员id
	 * @param myMemberId    我的盟员id
	 * @return
	 */
	List<Map<String,Object>> listPayDetailParticularByUnionIdAndMemberId(Integer memberId, Integer myMemberId) throws Exception;

	/**
	 * 获取商机统计信息
	 * @param unionId
	 * @param busId
	 * @return
	 */
	Map<String,Object> getStatisticData(Integer unionId, Integer busId) throws Exception;

	/**
	 *	统计我的所有已被接受的，目前未支付或已支付的商机佣金总额
	 * @param busId	商家id
	 * @param isAccept	是否支付
	 * @return
	 */
	Double sumAcceptFromMy(Integer busId, Integer isAccept) throws Exception;

	/**
	 *	统计我的所有已被接受的，目前未支付或已支付的商机佣金总额  带联盟的
	 * @param unionId	商家id
	 * @param busId	商家id
	 * @param isAccept	是否支付
	 * @return
	 */
	Double sumAcceptFromMyByUnionId(Integer unionId, Integer busId, Integer isAccept) throws Exception;

	/**
	 * 查询我的所有已被接受的，但对方已退盟的坏账佣金总和
	 * @param busId
	 * @return
	 */
	Double sumFromMyInBadDebt(Integer busId) throws Exception;
}
