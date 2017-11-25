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
    public Docket memberGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("member")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-盟员")
                        .description("基于Swagger2实现")
                        .version("3.1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.member.controller"))
                //可通过"或"来匹配多个模块
                .paths(PathSelectors.regex("/.*"))
                .build();
    }

    @Bean
    public Docket mainGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("main")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-联盟")
                        .description("基于Swagger2实现")
                        .version("3.1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.main.controller"))
                //可通过"或"来匹配多个模块
                .paths(PathSelectors.regex("/.*"))
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
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.opportunity.controller"))
                //可通过"或"来匹配多个模块
                .paths(PathSelectors.regex("/.*"))
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
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.card.controller"))
                //可通过"或"来匹配多个模块
                .paths(PathSelectors.regex("/.*"))
                .build();
    }

    @Bean
    public Docket verifierGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("verifier")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-平台管理者")
                        .description("基于Swagger2实现")
                        .version("3.1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.verifier.controller"))
                //可通过"或"来匹配多个模块
                .paths(PathSelectors.regex("/.*"))
                .build();
    }

}
