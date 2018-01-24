package com.gt.union.user.introduction.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟商家简介 缓存键工具类
 *
 * @author linweicong
 * @version 2018-01-24 16:24:13
 */
public class UnionUserIntroductionCacheUtil {

    public static String getIdKey(Integer id) {
        return "unionUserIntroduction:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


}