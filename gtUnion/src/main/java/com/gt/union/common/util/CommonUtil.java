package com.gt.union.common.util;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;

/**
 * 通用工具类
 *
 * @author
 */
public class CommonUtil {
    private static final Logger logger = Logger.getLogger(CommonUtil.class);

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(
        ApplicationContext applicationContext) {
        CommonUtil.applicationContext = applicationContext;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return isNull(collection) || collection.size() < 1;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.size() < 1;
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 判断对象是否为空
     *
     * @param object
     * @return
     */
    public static boolean isEmpty(Object object) {
        boolean b = false;
        try {
            if (object instanceof Collection) {
                b = isEmpty((Collection<?>) object);
            } else if (object instanceof Map) {
                b = isEmpty((Map<?, ?>) object);
            }
            b = isNull(object) || "".equals(object);
        } catch (Exception e) {
            b = false;
            e.printStackTrace();
        }
        return b;
    }

    public static boolean isNotEmpty(Object object) {
        boolean b = false;
        try {
            if (object instanceof Collection) {
                b = isNotEmpty((Collection<?>) object);
            } else if (object instanceof Map) {
                b = isNotEmpty((Map<?, ?>) object);
            }
            b = isNotNull(object) && !"".equals(object);
        } catch (Exception e) {
            b = false;
            e.printStackTrace();
        }
        return b;
    }

    private static boolean isNull(Object obj) {
        return obj == null;
    }

    private static boolean isNotNull(Object obj) {
        return obj != null;
    }


    public static Integer toInteger(Object obj) {
        try {
            if (!isEmpty(obj)) {
                return Integer.parseInt(obj.toString());
            } else {
                throw new Exception("对象为空，转换失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转String
     *
     * @param obj
     */
    public static String toString(Object obj) {
        try {
            if (!isEmpty(obj)) {
                return obj.toString();
            } else {
                throw new Exception("对象为空，转换失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转Double
     *
     * @param obj
     */
    public static Double toDouble(Object obj) {
        try {
            if (!isEmpty(obj)) {
                return Double.parseDouble(obj.toString());
            } else {
                throw new Exception("对象为空，转换失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 判断浏览器
     *
     * @return 1:微信浏览器,99:其他浏览器
     */
    public static Integer judgeBrowser(HttpServletRequest request) {
        String ua = request.getHeader("user-agent")
            .toLowerCase();
        // 微信==1
        if (ua.indexOf("micromessenger") > 0) {
            return 1;
        } else {//其他==99
            return 99;
        }
    }
}
