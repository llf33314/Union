package com.gt.union.union.member.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 入盟申请 缓存键工具类
 *
 * @author linweicong
 * @version 2017-12-27 10:46:19
 */
public class UnionMemberJoinCacheUtil {
    public static final int TYPE_APPLY_MEMBER_ID = 1;
    public static final int TYPE_RECOMMEND_MEMBER_ID = 2;
    public static final int TYPE_UNION_ID = 3;

    public static String getIdKey(Integer id) {
        return "unionMemberJoin:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


    public static String getApplyMemberIdKey(Integer applyMemberId) {
        return "unionMemberJoin:applyMemberId:" + applyMemberId;
    }

    public static String getValidApplyMemberIdKey(Integer applyMemberId) {
        return "unionMemberJoin:applyMemberId:" + applyMemberId + ":valid";
    }

    public static String getInvalidApplyMemberIdKey(Integer applyMemberId) {
        return "unionMemberJoin:applyMemberId:" + applyMemberId + ":invalid";
    }

    public static String getRecommendMemberIdKey(Integer recommendMemberId) {
        return "unionMemberJoin:recommendMemberId:" + recommendMemberId;
    }

    public static String getValidRecommendMemberIdKey(Integer recommendMemberId) {
        return "unionMemberJoin:recommendMemberId:" + recommendMemberId + ":valid";
    }

    public static String getInvalidRecommendMemberIdKey(Integer recommendMemberId) {
        return "unionMemberJoin:recommendMemberId:" + recommendMemberId + ":invalid";
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionMemberJoin:unionId:" + unionId;
    }

    public static String getValidUnionIdKey(Integer unionId) {
        return "unionMemberJoin:unionId:" + unionId + ":valid";
    }

    public static String getInvalidUnionIdKey(Integer unionId) {
        return "unionMemberJoin:unionId:" + unionId + ":invalid";
    }

}