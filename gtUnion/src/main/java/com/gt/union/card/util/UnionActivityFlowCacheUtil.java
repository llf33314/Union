package com.gt.union.card.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动项目流程 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
public class UnionActivityFlowCacheUtil {
    public static final int TYPE_ACTIVITY_PROJECT_ID = 1;

    public static String getIdKey(Integer id) {
        return "unionActivityFlow:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getActivityProjectIdKey(Integer activityProjectId) {
        return "unionActivityFlow:activityProjectId:" + activityProjectId;
    }
}