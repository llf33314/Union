package com.gt.union.card.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.api.entity.result.UnionDiscountResult;
import com.gt.union.card.entity.UnionCard;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟卡信息 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionCardService extends IService<UnionCard> {

	/***
	 * 获取盟员的联盟卡列表
	 * @param page
	 * @param unionId
	 * @param busId
	 * @param cardNo
	 * @param phone
	 * @return
	 */
	Page selectListByUnionId(Page page, Integer unionId, Integer busId, String cardNo, String phone) throws Exception;

	/**
	 * 获取联盟卡信息
	 * @param no
	 * @param busId
	 * @return
	 */
	Map<String,Object> getUnionCardInfo(String no, Integer busId) throws Exception;


	/**
	 * 根据联盟卡号和商家id获取联盟卡信息
	 * @param cardNo	联盟卡号
	 * @param busId		商家id
	 * @return
	 */
	Map<String,Object> getUnionCardInfoByCardNo(String cardNo, Integer busId);

	/**
	 * 根据手机号和商家id获取联盟卡信息
	 * @param phone		手机号
	 * @param busId		商家id
	 * @return
	 */
	Map<String,Object> getUnionCardInfoByPhone(String phone, Integer busId);

	/**
	 * 用户id和商家id获取联盟折扣
	 * @param memberId
	 * @param phone
	 *@param busId  @return
	 * @throws Exception
	 */
	UnionDiscountResult getConsumeUnionDiscount(Integer memberId, String phone, Integer busId) throws Exception;

	/**
	 * 根据unionCardRootId查询
	 * @param unionCardRootId 联盟卡主表id
	 * @return
	 */
	List<UnionCard> listByCardRootId(Integer unionCardRootId) throws Exception;

	/**
	 * 根据用户id和盟员Ids查询用户绑定的联盟卡信息列表
	 * @param memberId	用户id
	 * @param memberids	盟员Ids
	 * @return
	 */
	List<UnionCard> getByBusMemberIdAndMemberIds(Integer memberId, List<Integer> memberids);

	/**
	 * 根据rootId和盟员id查询联盟卡
	 * @param rootId
	 * @param memberId
	 * @return
	 */
	UnionCard getByUnionCardRootIdAndMemberId(Integer rootId, Integer memberId);

	/**
	 * 根据联盟卡id获取
	 * @param cardId
	 * @return
	 */
	UnionCard getById(Integer cardId);
}
