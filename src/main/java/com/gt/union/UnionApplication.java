package com.gt.union;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Created by Administrator on 2017/7/19 0019.
 */
@SpringBootApplication
@ServletComponentScan
@EnableAutoConfiguration
@MapperScan("com.gt.union.mapper")
public class UnionApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder) {
        return springApplicationBuilder.sources(UnionApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(UnionApplication.class, args);
    }

}
