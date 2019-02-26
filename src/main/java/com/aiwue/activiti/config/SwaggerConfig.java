package com.aiwue.note.config;


import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
/**
 * cf
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.aiwue"))
                .paths(PathSelectors.any())
                .build();


    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("系统相关 RESTful APIs")
                .termsOfServiceUrl("")
                .version("1.0")
                .build();
    }

}
