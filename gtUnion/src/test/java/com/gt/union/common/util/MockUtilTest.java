package com.gt.union.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/16 0016.
 */
public class MockUtilTest {

    @Test
    public void testGetOne() {
        Map<String, List<Object>> field2SourceListMap = new HashMap<>();
        final List<Object> iSourceList = new ArrayList() {
            {
                add(0);
                add(1);
            }
        };
        field2SourceListMap.put("i", iSourceList);
        final List<Object> listsSourceList = new ArrayList() {
            {
                for (int i = 0; i < 5; i++) {
                    final List<Object> listsSource = new ArrayList() {
                        {
                            add(MockUtil.get(String.class));
                            add(MockUtil.get(String.class));
                        }
                    };
                    add(listsSource);
                }
            }
        };

        field2SourceListMap.put("lists", listsSourceList);
        List<MockUtilTestBean> beanList = MockUtil.list(MockUtilTestBean.class, 100, field2SourceListMap);
        System.out.println(JSONArray.toJSONString(beanList, SerializerFeature.WriteMapNullValue));

    }
}
