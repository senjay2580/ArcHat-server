package com.senjay.archat.common.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenApiCustomizer globalHeaderCustomizer() {
        return openApi -> {
            Parameter tokenHeader = new Parameter()
                    .in(ParameterIn.HEADER.toString())
                    .schema(new StringSchema())
                    .name("Authorization")
                    .description("认证令牌")
                    .required(false);

//            Parameter requestIdHeader = new Parameter()
//                    .in(ParameterIn.HEADER.toString())
//                    .schema(new StringSchema())
//                    .name("X-Request-ID")
//                    .description("请求唯一标识")
//                    .required(false);
            // 添加到每个接口
            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(operation -> {
                        operation.addParametersItem(tokenHeader);
//                        operation.addParametersItem(requestIdHeader);
                    }));
        };
    }
}
