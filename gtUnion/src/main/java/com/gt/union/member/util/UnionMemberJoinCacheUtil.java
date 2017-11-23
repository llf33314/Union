package com.gt.union.member.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 入盟申请 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 10:22:05
 */
public class UnionMemberJoinCacheUtil {
    public static final int TYPE_UNION_ID = 1;
    public static final int TYPE_APPLY_MEMBER_ID = 2;
    public static final int TYPE_RECOMMEND_MEMBER_ID = 3;
    
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

    public static String getRecommendMemberIdKey(Integer recommendMemberId) {
        return "unionMemberJoin:recommendMemberId:" + recommendMemberId;
    }
    
    public static String getUnionIdKey(Integer unionId) {
        return "unionMemberJoin:unionId:" + unionId;
    }
    
}