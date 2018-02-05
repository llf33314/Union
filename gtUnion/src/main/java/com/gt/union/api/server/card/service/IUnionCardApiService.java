package com.gt.union.api.server.card.service;


import com.gt.union.api.entity.result.UnionDiscountResult;

/**
 * @author hongjiye
 * @time 2017-12-22 10:29
 **/
public interface IUnionCardApiService {

    /**
     * 根据手机号和商家id获取联盟折扣
     *
     * @param phone 手机号
     * @param busId 商家id
     * @return
     * @throws Exception 统一异常处理
     */
    UnionDiscountResult getConsumeUnionCardDiscount(String phone, Integer busId) throws Exception;
}
