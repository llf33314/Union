package com.gt.union.card.util;

import java.util.ArrayList;
import java.util.List;

/**
 * ERP文本项目 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public class UnionProjectErpTextCacheUtil {
    public static final int TYPE_ACTIVITY_PROJECT_ID = 1;

    public static String getIdKey(Integer id) {
        return "unionProjectErpText:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }
    
    public static String getActivityProjectId(Integer activityProjectId) {
        return "unionProjectErpText:activityProjectId:" + activityProjectId;
    }
}