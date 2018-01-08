package com.gt.union.opportunity.brokerage.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 佣金提现 缓存键工具类
 *
 * @author linweicong
 * @version 2017-12-27 10:56:34
 */
public class UnionBrokerageWithdrawalCacheUtil {
    public static final int TYPE_BUS_ID = 1;
    public static final int TYPE_VERIFIER_ID = 2;

    public static String getIdKey(Integer id) {
        return "unionBrokerageWithdrawal:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


    public static String getBusIdKey(Integer busId) {
        return "unionBrokerageWithdrawal:busId:" + busId;
    }

    public static String getValidBusIdKey(Integer busId) {
        return "unionBrokerageWithdrawal:busId:" + busId + ":valid";
    }

    public static String getInvalidBusIdKey(Integer busId) {
        return "unionBrokerageWithdrawal:busId:" + busId + ":invalid";
    }

    public static String getVerifierIdKey(Integer verifierId) {
        return "unionBrokerageWithdrawal:verifierId:" + verifierId;
    }

    public static String getValidVerifierIdKey(Integer verifierId) {
        return "unionBrokerageWithdrawal:verifierId:" + verifierId + ":valid";
    }

    public static String getInvalidVerifierIdKey(Integer verifierId) {
        return "unionBrokerageWithdrawal:verifierId:" + verifierId + ":invalid";
    }

}