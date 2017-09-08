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
 * Created by Administrator on 2017/7/19 0019.
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
                .paths(PathSelectors.regex("/.*")) //可通过"或"来匹配多个模块
                .build();
    }

    @Bean
    public Docket brokerageGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("brokerage")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-佣金相关")
                        .description("基于Swagger2实现")
                        .version("1.0.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.brokerage.controller"))
                .paths(PathSelectors.regex("/.*")) //可通过"或"来匹配多个模块
                .build();
    }

    @Bean
    public Docket cardGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("card")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-联盟卡相关")
                        .description("基于Swagger2实现")
                        .version("1.0.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.card.controller"))
                .paths(PathSelectors.regex("/.*")) //可通过"或"来匹配多个模块
                .build();
    }

    @Bean
    public Docket consumeGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("consume")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-消费核销相关")
                        .description("基于Swagger2实现")
                        .version("1.0.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.consume.controller"))
                .paths(PathSelectors.regex("/.*")) //可通过"或"来匹配多个模块
                .build();
    }

    @Bean
    public Docket indexGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("index")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-首页相关")
                        .description("基于Swagger2实现")
                        .version("1.0.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.index.controller"))
                .paths(PathSelectors.regex("/.*")) //可通过"或"来匹配多个模块
                .build();
    }

    @Bean
    public Docket mainGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("main")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-联盟相关")
                        .description("基于Swagger2实现")
                        .version("1.0.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.main.controller"))
                .paths(PathSelectors.regex("/.*")) //可通过"或"来匹配多个模块
                .build();
    }

    @Bean
    public Docket memberGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("member")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-盟员相关")
                        .description("基于Swagger2实现")
                        .version("1.0.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.member.controller"))
                .paths(PathSelectors.regex("/.*")) //可通过"或"来匹配多个模块
                .build();
    }

    @Bean
    public Docket opportunityGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("opportunity")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-商机相关")
                        .description("基于Swagger2实现")
                        .version("1.0.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.opportunity.controller"))
                .paths(PathSelectors.regex("/.*")) //可通过"或"来匹配多个模块
                .build();
    }

    @Bean
    public Docket preferentialGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("preferential")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-优惠服务相关")
                        .description("基于Swagger2实现")
                        .version("1.0.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.preferential.controller"))
                .paths(PathSelectors.regex("/.*")) //可通过"或"来匹配多个模块
                .build();
    }

    @Bean
    public Docket verifierGroupConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("verifier")
                .apiInfo(new ApiInfoBuilder()
                        .title("Restful文档接口服务平台-管理人员相关")
                        .description("基于Swagger2实现")
                        .version("1.0.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gt.union.verifier.controller"))
                .paths(PathSelectors.regex("/.*")) //可通过"或"来匹配多个模块
                .build();
    }
}
