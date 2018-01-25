package com.gt.union.common.util;

import java.lang.reflect.*;
import java.util.*;

/**
 * 模拟数据工具类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
public class MockUtil {
    private static final int DEFAULT_LIST_SITE = 3;

    private static final String BASIC_BYTE = "byte";
    private static final String BASIC_SHORT = "short";
    private static final String BASIC_INT = "int";
    private static final String BASIC_LONG = "long";
    private static final String BASIC_FLOAT = "float";
    private static final String BASIC_DOUBLE = "double";
    private static final String BASIC_CHAR = "char";
    private static final String BASIC_BOOLEAN = "boolean";
    private static final String OBJECT_BYTE = "java.lang.Byte";
    private static final String OBJECT_SHORT = "java.lang.Short";
    private static final String OBJECT_INTEGER = "java.lang.Integer";
    private static final String OBJECT_LONG = "java.lang.Long";
    private static final String OBJECT_FLOAT = "java.lang.Float";
    private static final String OBJECT_DOUBLE = "java.lang.Double";
    private static final String OBJECT_CHARACTER = "java.lang.Character";
    private static final String OBJECT_BOOLEAN = "java.lang.Boolean";
    private static final String OBJECT_CHAR_SEQUENCE = "java.lang.CharSequence";
    private static final String OBJECT_STRING = "java.lang.String";
    private static final String OBJECT_DATE = "java.util.Date";
    private static final String OBJECT_LIST = "java.util.List";
    private static final Set<String> BASE_DATA_TYPE_SET = new HashSet<String>() {
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
            add(OBJECT_DATE);
        }
    };
    private static final Map<String, List<Object>> DEFAULT_SOURCE_MAP = new HashMap<String, List<Object>>() {
        {
            put("delStatus", new ArrayList<Object>() {
                {
                    add(0);
                    add(1);
                }
            });
            put("delStatus", new ArrayList<Object>() {
                {
                    add(0);
                    add(1);
                }
            });
            put("joinType", new ArrayList<Object>() {
                {
                    add(1);
                    add(2);
                }
            });
            put("isIntegral", new ArrayList<Object>() {
                {
                    add(0);
                    add(1);
                }
            });
            put("isUnionOwner", new ArrayList<Object>() {
                {
                    add(0);
                    add(1);
                }
            });
            put("status", new ArrayList<Object>() {
                {
                    add(1);
                    add(2);
                    add(3);
                    add(4);
                }
            });
            put("type", new ArrayList<Object>() {
                {
                    add(1);
                    add(2);
                    add(3);
                }
            });
            put("isIntegral", new ArrayList<Object>() {
                {
                    add(0);
                    add(1);
                }
            });
            put("orderStatus", new ArrayList<Object>() {
                {
                    add(1);
                    add(2);
                    add(3);
                }
            });
            put("payType", new ArrayList<Object>() {
                {
                    add(1);
                    add(2);
                }
            });
            put("confirmStatus", new ArrayList<Object>() {
                {
                    add(1);
                    add(2);
                    add(3);
                }
            });
            put("acceptStatus", new ArrayList<Object>() {
                {
                    add(1);
                    add(2);
                    add(3);
                }
            });
            put("isClose", new ArrayList<Object>() {
                {
                    add(0);
                    add(1);
                }
            });
            put("isProjectCheck", new ArrayList<Object>() {
                {
                    add(0);
                    add(1);
                }
            });
            put("payStatus", new ArrayList<Object>() {
                {
                    add(1);
                    add(2);
                    add(3);
                    add(4);
                }
            });
        }
    };

    /**
     * 获取单个模拟对象
     *
     * @param clazz class类
     * @param <T>   泛型
     * @return T
     */
    public static <T> T get(Class<T> clazz) {
        return get(clazz, DEFAULT_SOURCE_MAP);
    }

    /**
     * 获取单个模拟对象
     *
     * @param clazz            class类
     * @param field2SrcListMap 属性到模拟数据源的映射
     * @param <T>              泛型
     * @return T
     */
    public static <T> T get(Class<T> clazz, Map<String, List<Object>> field2SrcListMap) {
        return mock(clazz, field2SrcListMap);
    }

    /**
     * 获取模拟对象列表
     *
     * @param clazz class类
     * @param size  数量
     * @param <T>   泛型
     * @return T
     */
    public static <T> List<T> list(Class<T> clazz, int size) {
        return list(clazz, size, DEFAULT_SOURCE_MAP);
    }

    /**
     * 获取模拟对象列表
     *
     * @param clazz            class类
     * @param size             数量
     * @param field2SrcListMap 属性到模拟数据源的映射
     * @param <T>              泛型
     * @return T
     */
    public static <T> List<T> list(Class<T> clazz, int size, Map<String, List<Object>> field2SrcListMap) {
        List<T> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(get(clazz, field2SrcListMap));
        }
        return result;
    }

    private static <T> T mock(Class<T> clazz, Map<String, List<Object>> field2SrcListMap) {
        Random random = new Random(System.currentTimeMillis());
        return mockData(clazz, field2SrcListMap, random);
    }

    @SuppressWarnings("unchecked")
    private static <T> T mockData(Class<T> clazz, Map<String, List<Object>> field2SrcListMap, Random random) {
        if (BASE_DATA_TYPE_SET.contains(clazz.getName())) {
            return mockData4BaseDataType(clazz, random);
        }

        T result;
        try {
            //防止构造函数私有化而报错
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
                Object obj = mockData4Field(field, field2SrcListMap, random);
                field.set(result, obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T mockData4BaseDataType(Class<T> clazz, Random random) {
        switch (clazz.getName()) {
            case BASIC_BYTE:
            case OBJECT_BYTE:
                return (T) (Object) (byte) random.nextInt(Byte.MAX_VALUE);
            case BASIC_SHORT:
            case OBJECT_SHORT:
                return (T) (Object) (short) random.nextInt(Byte.MAX_VALUE);
            case BASIC_INT:
            case OBJECT_INTEGER:
                return (T) (Object) random.nextInt(Byte.MAX_VALUE);
            case BASIC_LONG:
            case OBJECT_LONG:
                return (T) (Object) random.nextLong();
            case BASIC_FLOAT:
            case OBJECT_FLOAT:
                return (T) (Object) random.nextFloat();
            case BASIC_DOUBLE:
            case OBJECT_DOUBLE:
                return (T) (Object) random.nextDouble();
            case BASIC_CHAR:
            case OBJECT_CHARACTER:
                return (T) (Object) (char) random.nextInt(Byte.MAX_VALUE);
            case BASIC_BOOLEAN:
            case OBJECT_BOOLEAN:
                return (T) (Object) random.nextBoolean();
            case OBJECT_CHAR_SEQUENCE:
            case OBJECT_STRING:
                return (T) (Object) ("mock数据" + random.nextInt(Byte.MAX_VALUE));
            case OBJECT_DATE:
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, random.nextInt(365));
                return (T) (Object) calendar.getTime();
            default:
                return null;
        }
    }

    private static Object mockData4Field(Field field, Map<String, List<Object>> field2SrcListMap, Random random) {
        if (field2SrcListMap != null && field2SrcListMap.containsKey(field.getName())) {
            //是否指定随机来源范围
            List<Object> sourceList = field2SrcListMap.get(field.getName());
            return ListUtil.isEmpty(sourceList) ? null : sourceList.get(random.nextInt(sourceList.size()));
        }
        if (BASE_DATA_TYPE_SET.contains(field.getType().getName())) {
            //是否是基本数据类型
            return mockData4BaseDataType(field.getType(), random);
        }
        if (OBJECT_LIST.equals(field.getType().getName())) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type[] types = parameterizedType.getActualTypeArguments();
            Class clazz = (Class) (types[0]);
            return mockData4ListType(clazz, field2SrcListMap, random);
        }

        return mockData(field.getType(), field2SrcListMap, random);
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> mockData4ListType(Class<T> clazz, Map<String, List<Object>> field2SrcListMap, Random random) {
        List<T> result = new ArrayList();

        for (int i = 0; i < DEFAULT_LIST_SITE; i++) {
            result.add(mockData(clazz, field2SrcListMap, random));
        }

        return result;
    }

}
