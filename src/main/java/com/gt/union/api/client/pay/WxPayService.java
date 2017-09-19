package com.gt.union.api.client.pay;


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
    public int pay(Map<String, Object> param);


    /**
     * 提现接口
     * @param busId 商家id
     * @param memberId  粉丝id
     * @param openid
     * @param fee   提现金额  @return
     * @param verifierId
     * @throws Exception
     */
    int enterprisePayment(Integer busId, Integer memberId, String openid, Double fee, Integer verifierId) throws Exception;
}
