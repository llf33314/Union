package com.gt.union.common.util;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Administrator on 2017/10/16 0016.
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

    public static final <T> T getOne(Class<T> clazz) {
        T result = generateData(clazz, new HashSet<String>());
        return result;
    }

    public static final <T> List<T> get(Class<T> clazz, int size) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(getOne(clazz));
        }
        return result;
    }

    private static <T> T generateData(Class<T> clazz, Set<String> nonBasicClassSet) {
        if (nonBasicClassSet.contains(clazz.getName())) {
            return null;
        }
        nonBasicClassSet.add(clazz.getName());
        T result = null;
        Random random = new Random(System.currentTimeMillis());
        try {
            result = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isEnumConstant()) {
                    continue;
                }
                field.setAccessible(true);
                switch (field.getType().getName()) {
                    case BASIC_BYTE:
                    case OBJECT_BYTE:
                        field.set(result, (byte) random.nextInt(Byte.MAX_VALUE));
                        break;
                    case BASIC_SHORT:
                    case OBJECT_SHORT:
                        field.set(result, (short) random.nextInt(Byte.MAX_VALUE));
                        break;
                    case BASIC_INT:
                    case OBJECT_INTEGER:
                        field.set(result, random.nextInt(Byte.MAX_VALUE));
                        break;
                    case BASIC_LONG:
                    case OBJECT_LONG:
                        field.set(result, random.nextLong());
                        break;
                    case BASIC_FLOAT:
                    case OBJECT_FLOAT:
                        field.set(result, random.nextFloat());
                        break;
                    case BASIC_DOUBLE:
                    case OBJECT_DOUBLE:
                        field.set(result, random.nextDouble());
                        break;
                    case BASIC_CHAR:
                    case OBJECT_CHARACTER:
                        field.set(result, (char) random.nextInt(Byte.MAX_VALUE));
                        break;
                    case BASIC_BOOLEAN:
                    case OBJECT_BOOLEAN:
                        field.set(result, random.nextBoolean());
                        break;
                    case OBJECT_CHAR_SEQUENCE:
                    case OBJECT_STRING:
                        field.set(result, "测试数据" + random.nextInt(Byte.MAX_VALUE));
                        break;
                    case OBJECT_DATE:
                        field.set(result, Calendar.getInstance().getTime());
                        break;
                    case OBJECT_LIST:
                        field.set(result, new ArrayList<>());
                        break;
                    default:
                        field.set(result, generateData(field.getType(), nonBasicClassSet));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
