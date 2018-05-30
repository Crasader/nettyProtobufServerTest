package com.edaijia.drivertraceservice.common.config.swagger;

import com.edaijia.drivertraceservice.Application;
import io.swagger.annotations.Api;
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
 * @author zhoutao
 * @date 2018/3/5
 * <p>
 * SpringFox-swagger2生成api文档,减少团队之间的沟通成本
 * 先利用 SpringFox 库生成实时文档 http://host:port/swagger-ui.html
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(true)
                .forCodeGeneration(false)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(Application.NAME + "接口api文档")
                .version("1.0")
                .build();
    }
}
