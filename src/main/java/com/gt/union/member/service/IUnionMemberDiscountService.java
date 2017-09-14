package com.gt.union.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.member.entity.UnionMemberDiscount;

import java.util.List;

/**
 * <p>
 * 盟员折扣 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMemberDiscountService extends IService<UnionMemberDiscount> {
	//-------------------------------------------------- get ----------------------------------------------------------

	/**
	 * 查询最低折扣
	 * @param members   我的盟员身份列表
	 * @param memberList    和我有盟员关系的其他盟员列表
	 * @return
	 */
	UnionMemberDiscount getMinDiscountByMemberList(List<Integer> members, List<Integer> memberList);

    /**
     * 根据设置折扣盟员id和受惠折扣盟员id，获取折扣信息
     *
     * @param fromMemberId {not null} 设置折扣盟员id
     * @param toMemberId   {not null} 受惠折扣盟员id
     * @return
     * @throws Exception
     */
    UnionMemberDiscount getByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId) throws Exception;

	//------------------------------------------ list(include page) ---------------------------------------------------
	//------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据商家id、商家盟员身份id、被设置折扣的盟员身份id以及设置的折扣信息，更新或保存折扣信息
     *
     * @param busId       {not null} 商家id
     * @param memberId    {not null} 操作人的盟员身份id
     * @param tgtMemberId {not null} 被设置的折扣信息
     * @param discount
     * @throws Exception
     */
    void updateOrSaveDiscountByBusIdAndMemberId(Integer busId, Integer memberId, Integer tgtMemberId, Double discount) throws Exception;

    //------------------------------------------------- save ----------------------------------------------------------
	//------------------------------------------------- count ---------------------------------------------------------
	//------------------------------------------------ boolean --------------------------------------------------------
}
