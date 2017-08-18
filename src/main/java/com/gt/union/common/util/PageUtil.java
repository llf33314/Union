package com.gt.union.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/18 0018.
 */
public class PageUtil {
    private List records = null; //分页记录数据
    private int size = 10; //默认每页记录数
    private int current = 1; //默认当前页数

    public PageUtil() {
    }

    public void setRecords(List records) {
        this.records = records;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    //此工具类最终调用该方法返回结果
    public Map<String, Object> getMapAsPage() {
        // 计算总记录数
        int total = ListUtil.isEmpty(records) ? 0 : records.size();
        // 计算总页数
        int pages = total % size == 0 ? total/size : total/size + 1;
        // 选择要用的分页数据
        int offsetCurrent = size * (current - 1);
        int offsetEnd = current * size > total ? total : current * size;
        List _records = new ArrayList<>(size);
        for (int i = offsetCurrent; i < offsetEnd; i++) {
            _records.add(records.get(i));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("pages", pages);
        map.put("records", _records);
        map.put("size", size);
        map.put("current", current);

        return map;
    }
}
