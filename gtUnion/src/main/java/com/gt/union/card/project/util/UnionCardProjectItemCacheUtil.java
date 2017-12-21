package com.gt.union.card.project.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目优惠 缓存键工具类
 *
 * @author linweicong
 * @version 2017-11-27 09:55:47
 */
public class UnionCardProjectItemCacheUtil {
    public static final int TYPE_PROJECT_ID = 1;

    public static String getIdKey(Integer id) {
        return "unionCardProjectItem:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }
    
    public static String getProjectIdKey(Integer projectId) {
        return "unionCardProjectItem:projectId:" + projectId;
    }
}