package com.gt.union.card.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.api.bean.session.Member;
import com.gt.union.api.entity.result.UnionDiscountResult;
import com.gt.union.card.entity.UnionCard;
import com.gt.union.card.vo.UnionCardBindParamVO;
import com.gt.union.member.entity.UnionMember;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

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
	 * @param unionId
	 * @return
	 */
	Map<String,Object> getUnionCardInfo(String no, Integer busId, Integer unionId) throws Exception;


	/**
	 * 根据联盟卡号和商家id获取联盟卡信息
	 * @param cardNo    联盟卡号
	 * @param busId        商家id
	 * @param unionId
	 * @return
	 */
	Map<String,Object> getUnionCardInfoByCardNo(String cardNo, Integer busId, Integer unionId) throws Exception;

	/**
	 * 根据手机号和商家id获取联盟卡信息
	 * @param phone        手机号
	 * @param busId        商家id
	 * @param unionId
	 * @return
	 */
	Map<String,Object> getUnionCardInfoByPhone(String phone, Integer busId, Integer unionId) throws Exception;

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

	/**
	 * 根据rootId和盟员id列表查询联盟卡列表
	 * @param rootId
	 * @param memberIds
	 * @return
	 */
	List<UnionCard> listByCardRootIdAndMemberIds(Integer rootId, List<Integer> memberIds);

	/**
	 *	根据以下信息获取最低折扣信息
	 * @param rootId    联盟卡rootId
	 * @param busId        消费的商家id
	 * @param members    联盟id列表
	 * @param unionId
	 * @return
	 */
	Map<String,Object> getByMinDiscountByCard(Integer rootId, Integer busId, List<UnionMember> members, Integer unionId) throws Exception;

	/**
	 * 根据商家id和手机号获取验证码，并验证手机号是否升级过联盟卡
	 * @param busId
	 * @param phone
	 */
	void getPhoneCode(Integer busId, String phone) throws Exception;

	/**
	 * 根据手机号和盟员列表查询升级的联盟卡列表信息
	 * @param phone
	 * @param members
	 * @return
	 */
	List<UnionCard> listByPhoneAndMembers(String phone, List<UnionMember> members);

	/**
	 * 获取可升级联盟卡的联盟信息
	 * @param busId
	 * @param phone
	 * @param code
	 * @param unionId
	 * @return
	 */
	Map<String,Object> getUnionInfoByPhone(Integer busId, String phone, String code, Integer unionId) throws Exception;

	/**
	 * 升级联盟卡
	 * @param vo
	 * @return
	 */
	Map<String,Object> bindCard(UnionCardBindParamVO vo) throws Exception;

	/**
	 * 根据手机号和盟员id获取升级的联盟卡
	 * @param phone
	 * @param memberId
	 * @param isValidity  是否过期
	 * @return
	 */
	UnionCard getByPhoneAndMemberId(String phone, Integer memberId, Boolean isValidity) throws Exception;

	/**
	 * 导出联盟卡的数据列表
	 * @param unionId
	 * @param busId
	 * @param cardNo
	 * @param phone
	 * @return
	 */
	List<Map<String,Object>> listByUnionId(Integer unionId, Integer busId, String cardNo, String phone) throws Exception;

	/**
	 * 导出联盟卡数据
	 * @param titles
	 * @param contentName
	 * @param list
	 * @return
	 */
	HSSFWorkbook exportCardList(String[] titles, String[] contentName, List<Map<String, Object>> list);

	/**
	 * 手机端获取联盟卡信息
	 * @param busId
	 * @param member
	 * @return
	 */
	Map<String, Object> getUnionCardIndex(Integer busId, Member member) throws Exception;

	/**
	 * 前台生成办理联盟卡支付二维码信息
	 * @param busId
	 * @param phone
	 * @param memberId
	 * @param unionId
	 * @param cardType
	 * @return
	 */
	Map<String,Object> createQRCode(Integer busId, String phone, Integer memberId, Integer unionId, Integer cardType, Integer isReturn, String returnUrl) throws Exception;

	/**
	 * 办理联盟卡成功回调
	 * @param encrypt
	 * @param only
	 */
	void payBindCardSuccess(String encrypt, String only) throws Exception;

	/**
	 * 绑定升级联盟卡手机号
	 * @param member
	 * @param busId
	 * @param phone
	 */
	void bindCardPhone(Member member, Integer busId, String phone);

	/**
	 *
	 * @param busId
	 * @param member
	 * @param unionId
	 * @param memberId
	 * @return
	 */
	Map<String,Object> getUnionInfoCardList(Integer busId, Member member, Integer unionId, Integer memberId) throws Exception;
}
