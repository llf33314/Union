package com.gt.union.union.main.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟 缓存键工具类
 *
 * @author linweicong
 * @version 2017-12-27 10:56:25
 */
public class UnionMainCacheUtil {

    public static String getIdKey(Integer id) {
        return "unionMain:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


}