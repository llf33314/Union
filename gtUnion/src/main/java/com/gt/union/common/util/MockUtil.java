package com.gt.union.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 模拟数据工具类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
public class MockUtil {
    public static final String BASIC_BYTE = "byte";
    public static final String BASIC_SHORT = "short";
    public static final String BASIC_INT = "int";
    public static final String BASIC_LONG = "long";
    public static final String BASIC_FLOAT = "float";
    public static final String BASIC_DOUBLE = "double";
    public static final String BASIC_CHAR = "char";
    public static final String BASIC_BOOLEAN = "boolean";
    public static final String OBJECT_BYTE = "java.lang.Byte";
    public static final String OBJECT_SHORT = "java.lang.Short";
    public static final String OBJECT_INTEGER = "java.lang.Integer";
    public static final String OBJECT_LONG = "java.lang.Long";
    public static final String OBJECT_FLOAT = "java.lang.Float";
    public static final String OBJECT_DOUBLE = "java.lang.Double";
    public static final String OBJECT_CHARACTER = "java.lang.Character";
    public static final String OBJECT_BOOLEAN = "java.lang.Boolean";
    public static final String OBJECT_CHAR_SEQUENCE = "java.lang.CharSequence";
    public static final String OBJECT_STRING = "java.lang.String";
    public static final String OBJECT_DATE = "java.util.Date";
    public static final String OBJECT_LIST = "java.util.List";
    private static final Set<String> baseDataTypeSet = new HashSet() {
        {
            add(BASIC_BYTE);
            add(BASIC_BYTE);
            add(BASIC_SHORT);
            add(BASIC_INT);
            add(BASIC_LONG);
            add(BASIC_FLOAT);
            add(BASIC_DOUBLE);
            add(BASIC_CHAR);
            add(BASIC_BOOLEAN);
            add(OBJECT_BYTE);
            add(OBJECT_SHORT);
            add(OBJECT_INTEGER);
            add(OBJECT_LONG);
            add(OBJECT_FLOAT);
            add(OBJECT_DOUBLE);
            add(OBJECT_CHARACTER);
            add(OBJECT_BOOLEAN);
            add(OBJECT_CHAR_SEQUENCE);
            add(OBJECT_STRING);
        }
    };

    public static <T> T get(Class<T> tClass) {
        return get(tClass, null);
    }

    public static <T> T get(Class<T> tClass, Map<String, List<Object>> field2SourceListMap) {
        return mock(tClass, field2SourceListMap);
    }

    public static <T> List<T> list(Class<T> tClass, int size) {
        return list(tClass, size, null);
    }

    public static <T> List<T> list(Class<T> tClass, int size, Map<String, List<Object>> field2SourceListMap) {
        List<T> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(get(tClass, field2SourceListMap));
        }
        return result;
    }

    private static <T> T mock(Class<T> tClass, Map<String, List<Object>> field2SourceListMap) {
        Random random = new Random(System.currentTimeMillis());
        Set<String> set = new HashSet<>();
        return mockData(tClass, field2SourceListMap, random, set);
    }

    private static <T> T mockData(Class<T> clazz, Map<String, List<Object>> field2SourceListMap, Random random, Set<String> set) {
        if (baseDataTypeSet.contains(clazz.getName())) {
            return (T) mockData4BaseDataType(clazz, random);
        }
        if (set.contains(clazz.getName())) {
            //防止A、B互相引用造成死循环
            return null;
        }
        set.add(clazz.getName());
        T result;
        try {
            //防止构造函数私有而报错
            Constructor defaultConstructor = clazz.getDeclaredConstructors()[0];
            defaultConstructor.setAccessible(true);
            Class[] paramTypeArray = defaultConstructor.getParameterTypes();
            Object[] paramArray = new Object[paramTypeArray.length];
            for (int i = 0; i < paramTypeArray.length; i++) {
                paramArray[i] = null;
            }
            result = (T) defaultConstructor.newInstance(paramArray);
        } catch (Exception e) {
            //不支持枚举类实例
            return null;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers())) {
                //不支持静态变量
                continue;
            }
            field.setAccessible(true);
            try {
                Object obj = mockData4Field(field, field2SourceListMap, random, set);
                field.set(result, obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    private static Object mockData4Field(Field field, Map<String, List<Object>> field2SourceListMap, Random random, Set<String> set) {
        if (field2SourceListMap != null && field2SourceListMap.containsKey(field.getName())) {
            //是否指定随机来源范围
            List<Object> sourceList = field2SourceListMap.get(field.getName());
            return ListUtil.isEmpty(sourceList) ? null : sourceList.get(random.nextInt(sourceList.size()));
        }
        if (baseDataTypeSet.contains(field.getType().getName())) {
            //是否是基本数据类型
            return mockData4BaseDataType(field.getType(), random);
        }
        return mockData(field.getType(), field2SourceListMap, random, set);
    }

    private static Object mockData4BaseDataType(Class tClass, Random random) {
        switch (tClass.getName()) {
            case BASIC_BYTE:
            case OBJECT_BYTE:
                return (byte) random.nextInt(Byte.MAX_VALUE);
            case BASIC_SHORT:
            case OBJECT_SHORT:
                return (short) random.nextInt(Byte.MAX_VALUE);
            case BASIC_INT:
            case OBJECT_INTEGER:
                return random.nextInt(Byte.MAX_VALUE);
            case BASIC_LONG:
            case OBJECT_LONG:
                return random.nextLong();
            case BASIC_FLOAT:
            case OBJECT_FLOAT:
                return random.nextFloat();
            case BASIC_DOUBLE:
            case OBJECT_DOUBLE:
                return random.nextDouble();
            case BASIC_CHAR:
            case OBJECT_CHARACTER:
                return (char) random.nextInt(Byte.MAX_VALUE);
            case BASIC_BOOLEAN:
            case OBJECT_BOOLEAN:
                return random.nextBoolean();
            case OBJECT_CHAR_SEQUENCE:
            case OBJECT_STRING:
                return "mock数据" + random.nextInt(Byte.MAX_VALUE);
            case OBJECT_DATE:
                return Calendar.getInstance().getTime();
            case OBJECT_LIST:
                return new ArrayList<>();
            default:
                return null;
        }
    }
}
