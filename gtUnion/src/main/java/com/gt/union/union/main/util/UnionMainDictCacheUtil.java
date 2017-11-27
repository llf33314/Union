package com.gt.union.union.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟入盟申请必填信息 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
public class UnionMainDictCacheUtil {
    public static final int TYPE_UNION_ID = 1;

    public static String getIdKey(Integer id) {
        return "unionMainDict:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getUnionInKey(Integer unionId) {
        return "unionMainDict:unionId:" + unionId;
    }
}