package com.gt.union.union.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟公告 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 15:18:52
 */
public class UnionMainNoticeCacheUtil {
    public static final int TYPE_UNION_ID = 1;

    public static String getIdKey(Integer id) {
        return "unionMainNotice:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getUnionIdKey(Integer unionId) {
        return "unionMainNotice:unionId:" + unionId;
    }
}