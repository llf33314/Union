package com.gt.union.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger配置类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket apiGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("api")
                .apiInfo(new ApiInfoBuilder()
                        .title("UNION对外服务Restful接口，签名秘钥key(SR36E9KIYDLIU1VB6WX20HCW494QL97T)")
                        .description("基于Swagger2实现")
                        .version("1.0.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.api.server"))
                //可通过"或"来匹配多个模块
                .paths(PathSelectors.regex("/.*"))
                .build();
    }

    @Bean
    public Docket unionGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("union")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-联盟")
                        .description("基于Swagger2实现")
                        .version("3.1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.union"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket opportunityGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("opportunity")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-商机")
                        .description("基于Swagger2实现")
                        .version("3.1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.opportunity"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket cardGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("card")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-联盟卡")
                        .description("基于Swagger2实现")
                        .version("3.1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.card"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket financeGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("finance")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-财务")
                        .description("基于Swagger2实现")
                        .version("3.1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.finance"))
                .paths(PathSelectors.any())
                .build();
    }


    @Bean
    public Docket unionApiGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("unionApi")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-财务")
                        .description("基于Swagger2实现")
                        .version("3.1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.api.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    @Bean
    public Docket h5GroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("h5")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-h5")
                        .description("基于Swagger2实现")
                        .version("3.1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.h5"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket erpGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("erp")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-h5")
                        .description("基于Swagger2实现")
                        .version("3.1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.api.erp"))
                .paths(PathSelectors.any())
                .build();
    }
}
