package com.relativesHelp.relativesHelp.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI relativesHelpOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RelativesHelp API")
                        .description("API cho hệ thống gia phả & sự kiện gia đình")
                        .version("v1"));
    }
}