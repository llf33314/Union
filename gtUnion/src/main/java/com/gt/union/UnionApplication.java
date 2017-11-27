package com.gt.union;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * 项目启动类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
@SpringBootApplication
@ServletComponentScan
@MapperScan("com.gt.union.*.*.mapper")
public class UnionApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder) {
        return springApplicationBuilder.sources(UnionApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(UnionApplication.class, args);
    }

}
