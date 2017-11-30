package com.gt.union.common.util;

import com.baomidou.mybatisplus.plugins.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具
 *
 * @author linweicong
 * @version 2017-11-30 10:03:58
 */
public class PageUtil {
    public static <T> Page<T> setRecord(Page<T> page, List<T> srcList) {
        List<T> recordList = new ArrayList<>();
        int i = page.getOffsetCurrent();
        int listSize = srcList.size();
        int j = 0;
        int pageSize = page.getSize();
        for (; i < listSize && j < pageSize; i++, j++) {
            recordList.add(srcList.get(i));
        }
        page.setRecords(recordList);
        page.setTotal(listSize);
        return page;
    }
}
