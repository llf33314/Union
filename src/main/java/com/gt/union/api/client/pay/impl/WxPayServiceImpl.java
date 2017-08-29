package com.gt.union.api.client.pay.impl;

import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 微信支付服务
 * Created by Administrator on 2017/8/22 0022.
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    @Override
    public int pay(Map<String,Object> param) {
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/payApi/6F6D9AD2/79B4DE7C/payapi.do";
        try {
            Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param),url, Map.class, CommonConstant.WXMP_SIGN_KEY);
            if(CommonUtil.isEmpty(result)){
                return 0;
            }
            if(CommonUtil.toInteger(result.get("code")) != 0){
                return 0;
            }
        }catch (Exception e){
            return 0;
        }
        return 1;
    }
}
