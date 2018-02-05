package com.gt.union.common.util;

import com.alibaba.fastjson.JSON;
import com.gt.api.util.SessionUtils;
import com.gt.union.h5.brokerage.vo.H5BrokerageUser;

import javax.servlet.http.HttpServletRequest;

/**
 * 商家联盟session工具
 *
 * @author linweicong
 * @version 2017-12-01 11:39:37
 */
public class UnionSessionUtil extends SessionUtils {
    final public static String SESSION_UNION_H5_BROKERAGE_USER = "union_h5_brokerage_user";

    /**
     * 获取h5佣金平台登录者
     *
     * @param request 请求对象
     * @return H5BrokerageUser
     */
    public static H5BrokerageUser getH5BrokerageUser(HttpServletRequest request) {
        try {
            Object obj = request.getSession().getAttribute(SESSION_UNION_H5_BROKERAGE_USER);
            if (obj != null) {
                return JSON.parseObject(obj.toString(), H5BrokerageUser.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存入h5佣金平台登录者
     *
     * @param request         请求对象
     * @param h5BrokerageUser h5佣金平台登录者
     */
    public static void setH5BrokerageUser(HttpServletRequest request, H5BrokerageUser h5BrokerageUser) {
        try {
            request.getSession().setAttribute(SESSION_UNION_H5_BROKERAGE_USER, JSON.toJSONString(h5BrokerageUser));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
