package com.gt.union.card.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.api.entity.result.UnionDiscountResult;
import com.gt.union.card.entity.UnionCard;

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


	UnionDiscountResult getConsumeUnionDiscount(Integer id, Integer reqdata) throws Exception;
}
