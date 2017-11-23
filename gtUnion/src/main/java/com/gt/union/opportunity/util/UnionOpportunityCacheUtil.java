package com.gt.union.opportunity.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 商机 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 16:56:17
 */
public class UnionOpportunityCacheUtil {
    public static final int TYPE_UNION_ID = 1;
    public static final int TYPE_FROM_MEMBER_ID = 2;
    public static final int TYPE_TO_MEMBER_ID = 3;

    public static String getIdKey(Integer id) {
        return "unionOpportunity:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionOpportunity:unionId:" + unionId;
    }

    public static String getFromMemberIdKey(Integer fromMemberId) {
        return "unionOpportunity:fromMemberId:" + fromMemberId;
    }

    public static String getToMemberIdKey(Integer toMemberId) {
        return "unionOpportunity:toMemberId:" + toMemberId;
    }
}