package com.gt.union.opportunity.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 佣金支出 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
public class UnionBrokeragePayCacheUtil {
    public static final int TYPE_FROM_BUS_ID = 1;
    public static final int TYPE_TO_BUS_ID = 2;
    public static final int TYPE_FROM_MEMBER_ID = 3;
    public static final int TYPE_TO_MEMBER_ID = 4;
    public static final int TYPE_UNION_ID = 5;
    public static final int TYPE_OPPORTUNITY_ID = 6;
    public static final int TYPE_VERIFIER_ID = 7;

    public static String getIdKey(Integer id) {
        return "unionBrokeragePay:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }
    
    public static String getFromBusIdKey(Integer fromBusId) {
        return "unionBrokeragePay:fromBusId:" + fromBusId;
    }

    public static String getToBusIdKey(Integer toBusId) {
        return "unionBrokeragePay:toBusId:" + toBusId;
    }

    public static String getFromMemberIdKey(Integer fromMemberId) {
        return "unionBrokeragePay:fromMemberId:" + fromMemberId;
    }

    public static String getToMemberIdKey(Integer toMemberId) {
        return "unionBrokeragePay:toMemberId:" + toMemberId;
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionBrokeragePay:unionId:" + unionId;
    }

    public static String getOpportunityIdKey(Integer opportunityId) {
        return "unionBrokeragePay:opportunityId:" + opportunityId;
    }

    public static String getVerifierIdKey(Integer verifierId) {
        return "unionBrokeragePay:verifierId:" + verifierId;
    }
}