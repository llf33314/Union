package com.gt.union.log;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public class ExceptionStackTest {
    @Test
    public void exceptionStackTest() {
        String lineSeparator = System.getProperty("line.separator");
        try {
            System.out.println(lineSeparator);
            int i = 1;
            int j = 0;
            System.out.println(i/j);
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] elements = e.getStackTrace();
            StringBuilder sb = new StringBuilder(e.getMessage()).append(lineSeparator);
            for (int i = 0; i < elements.length; i++) {
                StackTraceElement element = elements[i];
                sb.append("    at ").append(element.getClassName())
                        .append(".").append(element.getMethodName())
                        .append("(").append(element.getFileName())
                        .append(":").append(element.getLineNumber())
                        .append(")").append(lineSeparator);
            }
            System.out.println(sb.toString());
        }
    }
}
