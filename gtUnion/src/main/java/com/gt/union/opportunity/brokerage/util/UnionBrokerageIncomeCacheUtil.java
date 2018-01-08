package com.gt.union.opportunity.brokerage.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 佣金收入 缓存键工具类
 *
 * @author linweicong
 * @version 2017-12-27 10:56:25
 */
public class UnionBrokerageIncomeCacheUtil {
    public static final int TYPE_BUS_ID = 1;
    public static final int TYPE_MEMBER_ID = 2;
    public static final int TYPE_UNION_ID = 3;
    public static final int TYPE_CARD_ID = 4;
    public static final int TYPE_OPPORTUNITY_ID = 5;

    public static String getIdKey(Integer id) {
        return "unionBrokerageIncome:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


    public static String getBusIdKey(Integer busId) {
        return "unionBrokerageIncome:busId:" + busId;
    }

    public static String getValidBusIdKey(Integer busId) {
        return "unionBrokerageIncome:busId:" + busId + ":valid";
    }

    public static String getInvalidBusIdKey(Integer busId) {
        return "unionBrokerageIncome:busId:" + busId + ":invalid";
    }

    public static String getMemberIdKey(Integer memberId) {
        return "unionBrokerageIncome:memberId:" + memberId;
    }

    public static String getValidMemberIdKey(Integer memberId) {
        return "unionBrokerageIncome:memberId:" + memberId + ":valid";
    }

    public static String getInvalidMemberIdKey(Integer memberId) {
        return "unionBrokerageIncome:memberId:" + memberId + ":invalid";
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionBrokerageIncome:unionId:" + unionId;
    }

    public static String getValidUnionIdKey(Integer unionId) {
        return "unionBrokerageIncome:unionId:" + unionId + ":valid";
    }

    public static String getInvalidUnionIdKey(Integer unionId) {
        return "unionBrokerageIncome:unionId:" + unionId + ":invalid";
    }

    public static String getCardIdKey(Integer cardId) {
        return "unionBrokerageIncome:cardId:" + cardId;
    }

    public static String getValidCardIdKey(Integer cardId) {
        return "unionBrokerageIncome:cardId:" + cardId + ":valid";
    }

    public static String getInvalidCardIdKey(Integer cardId) {
        return "unionBrokerageIncome:cardId:" + cardId + ":invalid";
    }

    public static String getOpportunityIdKey(Integer opportunityId) {
        return "unionBrokerageIncome:opportunityId:" + opportunityId;
    }

    public static String getValidOpportunityIdKey(Integer opportunityId) {
        return "unionBrokerageIncome:opportunityId:" + opportunityId + ":valid";
    }

    public static String getInvalidOpportunityIdKey(Integer opportunityId) {
        return "unionBrokerageIncome:opportunityId:" + opportunityId + ":invalid";
    }

}