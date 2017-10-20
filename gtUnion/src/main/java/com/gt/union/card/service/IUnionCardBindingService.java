package com.gt.union.card.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.api.entity.result.UnionBindCardResult;
import com.gt.union.card.entity.UnionCard;
import com.gt.union.card.entity.UnionCardBinding;

import java.util.List;

/**
 * <p>
 * 联盟绑定表 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-11
 */
public interface IUnionCardBindingService extends IService<UnionCardBinding> {

	/**
	 * 绑定联盟卡
	 * @param busid		商家id
	 * @param id		粉丝用户id
	 * @param phone		电话号码
	 * @param code		验证码
	 * @return
	 * @throws Exception
	 */
	UnionBindCardResult bindUnionCard(Integer busid, Integer id, String phone, String code) throws Exception;

	/**
	 * 根据联盟卡rootId和粉丝用户id查询绑定的联盟卡
	 * @param rootId
	 * @param memberId
	 * @return
	 */
	UnionCardBinding getByCardRootIdAndMemberId(Integer rootId, Integer memberId);

	/**
	 * 根据粉丝用户id查询绑定的联盟卡
	 * @param memberId
	 * @return
	 */
	UnionCardBinding getByMemberId(Integer memberId);

	/**
	 * 插入联盟卡绑定
	 * @param rootId
	 * @param memberId
	 * @return
	 */
	UnionCardBinding createUnionCardBinding(Integer rootId, Integer memberId);
}
