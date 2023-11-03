package com.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 配置Swagger
 *
 * @author beamstark
 */
@Configuration
@EnableSwagger2
public class MySwaggerConfig {

    @Value("${swagger.enabled}")
    private Boolean enabled;

    /**
     * api接口包扫描路径
     */
    public static final String RECEPTION_CONTROLLER = "com.controller";
    public static final String VERSION = "1.0.0";
    public static final String TITLE = "aigc-pms";
    public static final String INTRO = "";

    //    @Bean("")
    public Docket receptionController() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enabled)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(RECEPTION_CONTROLLER))
                // 可以根据url路径设置哪些请求加入文档，忽略哪些请求
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //设置文档的标题
                .title(TITLE)
                // 设置文档的描述
                .description(INTRO)
                // 设置文档的版本信息-> 1.0.0 Version information
                .version(VERSION)
                // 设置文档的License信息->1.3 License information
                .termsOfServiceUrl("")
                .build();
    }
}
