package com.gt.union.opportunity.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 商机佣金比率 缓存键工具类
 *
 * @author linweicong
 * @version 2017-12-27 10:56:25
 */
public class UnionOpportunityRatioCacheUtil {
    public static final int TYPE_FROM_MEMBER_ID = 1;
    public static final int TYPE_TO_MEMBER_ID = 2;
    public static final int TYPE_UNION_ID = 3;

    public static String getIdKey(Integer id) {
        return "unionOpportunityRatio:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


    public static String getFromMemberIdKey(Integer fromMemberId) {
        return "unionOpportunityRatio:fromMemberId:" + fromMemberId;
    }

    public static String getValidFromMemberIdKey(Integer fromMemberId) {
        return "unionOpportunityRatio:fromMemberId:" + fromMemberId + ":valid";
    }

    public static String getInvalidFromMemberIdKey(Integer fromMemberId) {
        return "unionOpportunityRatio:fromMemberId:" + fromMemberId + ":invalid";
    }

    public static String getToMemberIdKey(Integer toMemberId) {
        return "unionOpportunityRatio:toMemberId:" + toMemberId;
    }

    public static String getValidToMemberIdKey(Integer toMemberId) {
        return "unionOpportunityRatio:toMemberId:" + toMemberId + ":valid";
    }

    public static String getInvalidToMemberIdKey(Integer toMemberId) {
        return "unionOpportunityRatio:toMemberId:" + toMemberId + ":invalid";
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionOpportunityRatio:unionId:" + unionId;
    }

    public static String getValidUnionIdKey(Integer unionId) {
        return "unionOpportunityRatio:unionId:" + unionId + ":valid";
    }

    public static String getInvalidUnionIdKey(Integer unionId) {
        return "unionOpportunityRatio:unionId:" + unionId + ":invalid";
    }

}