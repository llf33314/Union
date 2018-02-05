package com.gt.union.wxapp.card.util;

import com.gt.api.util.DESKeysUtil;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.wxapp.card.constant.WxAppCardConstant;

/**
 * @author hongjiye
 * @time 2017-12-29 16:14
 **/
public class WxAppCacheKeyUtil {

    /**
     * 解密后的key
     *
     * @param token
     * @param desKey
     * @return
     * @throws Exception
     */
    public static String getRedisMemberKeyByToken(String token, String desKey) throws Exception {
        try {
            token = (new DESKeysUtil(desKey)).getDesString(token);
        } catch (Exception e) {
            return null;
        }

        if (!token.startsWith(WxAppCardConstant.UNION_TOKEN_KEY)) {
            throw new BusinessException(CommonConstant.TOKEN_ERROR);
        } else {
            token = token.split(WxAppCardConstant.REDIS_MEMBER_LINK)[0];
            String redisUserKey = token.replace(WxAppCardConstant.UNION_TOKEN_KEY, "");
            return redisUserKey;
        }
    }
}
