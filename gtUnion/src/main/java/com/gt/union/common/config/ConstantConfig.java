package com.gt.union.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 常量配置类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
@Configuration
public class ConstantConfig {
    public static String UNION_URL;

    @Value("${union.url}")
    public void setUnionUrl(String unionUrl) {
        UNION_URL = unionUrl;
    }

}
