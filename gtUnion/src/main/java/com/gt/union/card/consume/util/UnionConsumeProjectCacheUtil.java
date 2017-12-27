package com.gt.union.card.consume.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费核销项目优惠 缓存键工具类
 *
 * @author linweicong
 * @version 2017-12-27 10:56:25
 */
public class UnionConsumeProjectCacheUtil {
    public static final int TYPE_CONSUME_ID = 1;
    public static final int TYPE_PROJECT_ITEM_ID = 2;
    public static final int TYPE_PROJECT_ID = 3;

    public static String getIdKey(Integer id) {
        return "unionConsumeProject:" + id;
    }

    public static List<String> getIdKey(List<Integer> idList) {
        List<String> result = new ArrayList<>();
        for (Integer id : idList) {
            result.add(getIdKey(id));
        }
        return result;
    }


    public static String getConsumeIdKey(Integer consumeId) {
        return "unionConsumeProject:consumeId:" + consumeId;
    }

    public static String getValidConsumeIdKey(Integer consumeId) {
        return "unionConsumeProject:consumeId:" + consumeId + ":valid";
    }

    public static String getInvalidConsumeIdKey(Integer consumeId) {
        return "unionConsumeProject:consumeId:" + consumeId + ":invalid";
    }

    public static String getProjectItemIdKey(Integer projectItemId) {
        return "unionConsumeProject:projectItemId:" + projectItemId;
    }

    public static String getValidProjectItemIdKey(Integer projectItemId) {
        return "unionConsumeProject:projectItemId:" + projectItemId + ":valid";
    }

    public static String getInvalidProjectItemIdKey(Integer projectItemId) {
        return "unionConsumeProject:projectItemId:" + projectItemId + ":invalid";
    }

    public static String getProjectIdKey(Integer projectId) {
        return "unionConsumeProject:projectId:" + projectId;
    }

    public static String getValidProjectIdKey(Integer projectId) {
        return "unionConsumeProject:projectId:" + projectId + ":valid";
    }

    public static String getInvalidProjectIdKey(Integer projectId) {
        return "unionConsumeProject:projectId:" + projectId + ":invalid";
    }

}