package com.gt.union.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 判断联盟信息的权限，需加该注解， 而且unionId不能为空
 * Created by Administrator on 2017/8/1 0001.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UnionMainAuthorityAnnotation {




}
