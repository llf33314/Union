package com.gt.union.union.member.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 盟员 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 10:22:05
 */
public class UnionMemberCacheUtil {
    public static final int TYPE_BUS_ID = 1;
    public static final int TYPE_UNION_ID = 2;

    public static String getIdKey(Integer id) {
        return "unionMember:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionMember:unionId:" + unionId;
    }

    public static String getBusIdKey(Integer busId) {
        return "unionMember:busId:" + busId;
    }
    
}