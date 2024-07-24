package com.example.project.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("중고 책 대여 서비스")
                .version("v0.0.1")
                .description("유저, 책 서비스 입니다.");
        return new OpenAPI()
                .components(new Components())
                .info(info);
    }

}