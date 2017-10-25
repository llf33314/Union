package com.gt.union.card.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.entity.UnionCardRoot;

/**
 * <p>
 * 联盟卡主信息 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionCardRootService extends IService<UnionCardRoot> {

	/**
	 * 根据手机号获取联盟主卡信息
	 * @param phone	手机号
	 * @return
	 * @throws Exception
	 */
	UnionCardRoot getByPhone(String phone) throws Exception;

	/**
	 * 根据联盟卡号获取联盟主卡信息
	 *
	 * @param cardNo	联盟卡号
	 * @return UnionCardRoot
	 * @throws Exception
	 */
	UnionCardRoot getByCardNo(String cardNo) throws Exception;

	/**
	 * 根据联盟卡主id获取联盟主卡信息
	 * @param rootId
	 * @return UnionCardRoot
	 */
	UnionCardRoot getById(Integer rootId);

	/**
	 * 添加root
	 * @param phone
	 * @return
	 */
	UnionCardRoot createUnionCardRoot(String phone);
}
