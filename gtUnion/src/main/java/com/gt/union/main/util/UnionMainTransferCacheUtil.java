package com.gt.union.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟转移 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
public class UnionMainTransferCacheUtil {
    public static final int TYPE_UNION_ID = 1;
    public static final int TYPE_FROM_MEMBER_ID = 2;
    public static final int TYPE_TO_MEMBER_ID = 3;

    public static String getIdKey(Integer id) {
        return "unionMainTransfer:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getUnionInKey(Integer unionId) {
        return "unionMainTransfer:unionId:" + unionId;
    }

    public static String getFromMemberIdKey(Integer fromMemberId) {
        return "unionMainTransfer:fromMemberId:" + fromMemberId;
    }

    public static String getToMemberIdKey(Integer toMemberId) {
        return "unionMainTransfer:toMemberId:" + toMemberId;
    }
}