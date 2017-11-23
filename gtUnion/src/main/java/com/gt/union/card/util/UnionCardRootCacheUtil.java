package com.gt.union.card.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟卡根信息 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:13
 */
public class UnionCardRootCacheUtil {

    public static String getIdKey(Integer id) {
        return "unionCardRoot:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }
}