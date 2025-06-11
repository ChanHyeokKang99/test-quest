package com.justteam.test_quest_api.swagger;


import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openAPI() {
        SecurityScheme apiKey = new SecurityScheme()
         .type(SecurityScheme.Type.HTTP)
         .in(SecurityScheme.In.HEADER)
         .name("Authorization")
         .scheme("bearer")
        .bearerFormat("JWT");

        Info info = new Info().title("Test Quest Board API Document")
        .version("v0.0.1")
                .description("TestQuest API 명세서입니다.");
        return new OpenAPI()
            .components(new Components().addSecuritySchemes("Bearer Token", apiKey))
                .info(info).servers(List.of());
    }
}
