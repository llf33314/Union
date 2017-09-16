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
	 * 根据手机号获取信息
	 * @param phone 手机号
	 * @return
	 */
	UnionCardRoot getByPhone(String phone) throws Exception;

	/**
	 * 根据联盟卡号获取信息
	 * @param cardNo
	 * @return
	 */
	UnionCardRoot getByCardNo(String cardNo) throws Exception;

	/**
	 *
	 * @param rootId
	 * @return
	 */
	UnionCardRoot getById(Integer rootId);
}
