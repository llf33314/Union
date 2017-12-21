package com.gt.union.card.project.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动项目流程 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
public class UnionCardProjectFlowCacheUtil {
    public static final int TYPE_PROJECT_ID = 1;

    public static String getIdKey(Integer id) {
        return "unionCardProjectFlow:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }

    public static String getProjectIdKey(Integer projectId) {
        return "unionCardProjectFlow:projectId:" + projectId;
    }
}