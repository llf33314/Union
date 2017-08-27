package com.gt.union.api.client.pay;

import java.util.Map;

/**
 * Created by Administrator on 2017/8/22 0022.
 */
public interface WxPayService {

    public int pay(Map<String,Object> param);
}
