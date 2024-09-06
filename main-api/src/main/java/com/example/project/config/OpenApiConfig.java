package com.example.project.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
@OpenAPIDefinition(servers = {
        @Server(url = "https://book-service-prod.jin900920.com/api", description = "main"),
        @Server(url = "/api", description = "main"),
        @Server(url = "http://localhost:8080/api", description = "local"),
        @Server(url = "http://book-service-prod.jin900920.com:8080/api", description = "deploy test")
})
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("중고 책 대여 서비스")
                .version("v0.0.1")
                .description("유저, 책 서비스 입니다.");
        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Token");
        return new OpenAPI()
                .components(
                        new Components().addSecuritySchemes("Bearer Token", apiKey)
                ).addSecurityItem(securityRequirement)
                .info(info);
    }

}