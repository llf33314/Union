package com.gt.union.api.client.pay;

import com.gt.union.entity.brokerage.UnionVerifyMember;

import java.util.Map;

/**
 * Created by Administrator on 2017/8/22 0022.
 */
public interface WxPayService {

    /**
     * 支付接口
     * @param param
     * @return
     */
    public int pay(Map<String,Object> param);


    /**
     * 提现接口
     * @param busid 商家id
     * @param memberId  粉丝id
     * @param openid
     * @param unionId
     *@param fee   提现金额  @return
     * @param unionVerifyMember   @throws Exception
     */
    int enterprisePayment(Integer busid, Integer memberId, String openid, Integer unionId, Double fee, UnionVerifyMember unionVerifyMember) throws Exception;
}
