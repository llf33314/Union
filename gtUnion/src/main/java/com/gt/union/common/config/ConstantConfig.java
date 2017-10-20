package com.gt.union.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2017/10/17 0017.
 */
@Configuration
public class ConstantConfig {
    public static String UNION_URL;

    @Value("${union.url}")
    public void setUnionUrl(String unionUrl) {
        UNION_URL = unionUrl;
    }

}
