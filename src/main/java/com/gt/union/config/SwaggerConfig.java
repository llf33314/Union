package com.gt.union.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by Administrator on 2017/7/19 0019.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket apiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("controller")
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.gt.union.controller"))
            .paths(PathSelectors.regex("/.*")) //可通过"或"来匹配多个模块
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Restful文档接口服务平台")
            .description("基于Swagger2实现")
            .version("1.0.0")
            .build();
    }
}
